package ru.mdm.files.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.api.FileRestApi;
import ru.mdm.files.model.dto.ContentWithMetadataDto;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;
import ru.mdm.files.service.FileService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * REST-контроллер для работы с файлами.
 */
@RestController
@RequestMapping(FileRestApi.BASE_PATH)
@RequiredArgsConstructor
public class FileController implements FileRestApi {

    private final FileService fileServiceImpl;

    @Override
    public Mono<FileMetadataDto> createFile(UploadFileMetadataDto metadataDto, Mono<FilePart> file, Boolean compress) {
        return Mono.usingWhen(
                file,
                filePart -> fileServiceImpl.createFile(metadataDto, filePart.content(), compress),
                Part::delete
        );
    }

    @Override
    public Mono<ResponseEntity<Flux<DataBuffer>>> getFileContent(UUID fileId) {
        return fileServiceImpl.getFileContent(fileId)
                .map(this::mapToContentResponseEntity);
    }

    @Override
    public Mono<FileMetadataDto> getFileMetadata(UUID fileId) {
        return null;
    }

    @Override
    public Mono<FileMetadataDto> updateFileMetadata(UUID fileId, UploadFileMetadataDto dto) {
        return null;
    }

    @Override
    public Mono<FileMetadataDto> deleteFile(UUID fileId) {
        return null;
    }

    @Override
    public Flux<FileMetadataDto> getFiles(Pageable pageable) {
        return null;
    }

    private ResponseEntity<Flux<DataBuffer>> mapToContentResponseEntity(ContentWithMetadataDto dto) {
        var headers = new HttpHeaders();
        headers.setContentLength(dto.getFileSize());
        headers.setContentType(MediaType.parseMediaType(dto.getMimeType()));

        var encodedFileName = URLEncoder.encode(dto.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        var dispositionValue = String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s", encodedFileName, encodedFileName);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, dispositionValue);

        return new ResponseEntity<>(dto.getContent(), headers, HttpStatus.OK);
    }
}
