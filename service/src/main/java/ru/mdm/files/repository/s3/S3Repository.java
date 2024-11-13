package ru.mdm.files.repository.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.configuration.S3Properties;
import ru.mdm.files.exception.ErrorCode;
import ru.mdm.files.exception.ServerException;
import ru.mdm.files.repository.ContentRepository;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Репозиторий для взаимодействия с S3-хранилищем.
 */
@Slf4j
@Repository
@Validated
@RequiredArgsConstructor
public class S3Repository implements ContentRepository {

    private static final String ORIGINAL_FILE_NAME = "originalFileName";
    private static final String X_AMZ_META = "x-amz-meta-";
    private static final int MIN_PART_SIZE = 5242880;

    private final S3AsyncClient s3Client;
    private final S3Properties s3Properties;

    @Override
    public Mono<String> createContent(String fileName, Flux<DataBuffer> data) { //TODO отрефакторить метод
        var meta = new ObjectMetadata();
        meta.addUserMetadata(X_AMZ_META + ORIGINAL_FILE_NAME, UriUtils.encode(fileName, "UTF-8"));

        var extension = FilenameUtils.getExtension(fileName);
        var s3FileName = UUID.randomUUID().toString() + FilenameUtils.EXTENSION_SEPARATOR + extension;
        var key = absolutify(s3Properties.getPrefix(), s3FileName);

        CompletableFuture<CreateMultipartUploadResponse> uploadRequest = s3Client
                .createMultipartUpload(CreateMultipartUploadRequest.builder()
                        .key(key)
                        .metadata(meta.getUserMetadata())
                        .bucket(s3Properties.getBucketName())
                        .build());

        var uploadState = new UploadState(s3Properties.getBucketName(), key);

        return Mono
                .fromFuture(uploadRequest)
                .flatMapMany(response -> {
                    checkResponse(response, ErrorCode.CANNOT_WRITE_FILE_TO_STORAGE);
                    uploadState.uploadId = response.uploadId();
                    log.debug("Загрузка файла. uploadId={}", response.uploadId());
                    return data;
                })
                .bufferUntil(buffer -> {
                    uploadState.buffered += buffer.readableByteCount();
                    if (uploadState.buffered >= MIN_PART_SIZE) {
                        log.debug("Буфер для загрузки: bufferedBytes={}, partCounter={}, uploadId={}",
                                uploadState.buffered, uploadState.partCounter, uploadState.uploadId);
                        uploadState.buffered = 0;
                        return true;
                    } else {
                        return false;
                    }
                })
                .map(this::concatBuffers)
                .flatMap(buffer -> uploadPart(uploadState, buffer))
                .onBackpressureBuffer()
                .reduce(uploadState, (state, completedPart) -> {
                    state.completedParts.put(completedPart.partNumber(), completedPart);
                    return state;
                })
                .flatMap(this::completeUpload)
                .map(response -> {
                    checkResponse(response, ErrorCode.CANNOT_WRITE_FILE_TO_STORAGE);
                    return key;
                });
    }

    @Override
    public Flux<DataBuffer> getContent(String contentRef) {
        return Mono.fromFuture(
                        s3Client.getObject(GetObjectRequest.builder()
                                        .bucket(s3Properties.getBucketName())
                                        .key(contentRef)
                                        .build(),
                                AsyncResponseTransformer.toPublisher())
                )
                .flatMapMany(responsePublisher ->
                        Flux.from(responsePublisher.map(DefaultDataBufferFactory.sharedInstance::wrap)));
    }

    @Override
    public Mono<Void> deleteContent(String contentRef) {
        return Mono.fromFuture(
                        s3Client.deleteObject(
                                DeleteObjectRequest.builder()
                                        .bucket(s3Properties.getBucketName())
                                        .key(contentRef)
                                        .build())
                )
                .doOnNext(response -> checkResponse(response, ErrorCode.CANNOT_DELETE_FILE_FROM_STORAGE))
                .then();
    }

    private static String absolutify(String prefix, String fileName) {
        if (StringUtils.hasLength(prefix)) {
            var location = prefix;
            if (location.startsWith("/")) {
                location = location.substring(1);
            }
            if (location.endsWith("/")) {
                location = location.substring(0, location.length() - 1);
            }
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            return String.format("%s/%s", location, fileName);
        }
        return fileName;
    }

    private void checkResponse(SdkResponse response, ErrorCode error) {
        var success = Optional.ofNullable(response)
                .map(SdkResponse::sdkHttpResponse)
                .map(SdkHttpResponse::isSuccessful)
                .orElse(Boolean.FALSE);
        if (Boolean.FALSE.equals(success)) {
            throw new ServerException(error.getText());
        }
    }

    private Mono<CompletedPart> uploadPart(UploadState uploadState, ByteBuffer buffer) {
        final int partNumber = ++uploadState.partCounter;
        log.debug("Загрузка части файла: partNumber={}, contentLength={}", partNumber, buffer.capacity());

        CompletableFuture<UploadPartResponse> request = s3Client.uploadPart(UploadPartRequest.builder()
                        .bucket(uploadState.bucket)
                        .key(uploadState.key)
                        .partNumber(partNumber)
                        .uploadId(uploadState.uploadId)
                        .contentLength((long) buffer.capacity())
                        .build(),
                AsyncRequestBody.fromByteBuffer(buffer));

        return Mono
                .fromFuture(request)
                .map(uploadPartResult -> {
                    checkResponse(uploadPartResult, ErrorCode.CANNOT_WRITE_FILE_TO_STORAGE);
                    log.debug("Загрузка части файла завершена: part={}, etag={}", partNumber, uploadPartResult.eTag());
                    return CompletedPart.builder()
                            .eTag(uploadPartResult.eTag())
                            .partNumber(partNumber)
                            .build();
                });
    }

    private Mono<CompleteMultipartUploadResponse> completeUpload(UploadState state) {
        log.debug("Загрузка файла завершена: bucket={}, key={}, completedParts.size={}",
                state.bucket, state.key, state.completedParts.size());

        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
                .parts(state.completedParts.values())
                .build();

        return Mono.fromFuture(s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(state.bucket)
                .uploadId(state.uploadId)
                .multipartUpload(multipartUpload)
                .key(state.key)
                .build()));
    }

    private ByteBuffer concatBuffers(List<DataBuffer> buffers) {
        log.debug("creating BytBuffer from {} chunks", buffers.size());

        var partSize = buffers.stream()
                .reduce(0, (integer, dataBuffer) -> integer + dataBuffer.readableByteCount(), Integer::sum);

        var partData = ByteBuffer.allocate(partSize);
        buffers.forEach(buffer -> partData.put(buffer.asByteBuffer()));
        partData.rewind();

        return partData;
    }

    private static class UploadState {
        final String bucket;
        final String key;
        String uploadId;
        int partCounter;
        Map<Integer, CompletedPart> completedParts = new HashMap<>();
        int buffered = 0;

        UploadState(String bucket, String key) {
            this.bucket = bucket;
            this.key = key;
        }
    }
}
