package ru.mdm.files.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.exception.ErrorCode;
import ru.mdm.files.exception.ResourceNotFoundException;
import ru.mdm.files.model.dto.ContentWithMetadataDto;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;
import ru.mdm.files.model.entity.File;
import ru.mdm.files.model.mapper.FileMapper;
import ru.mdm.files.repository.FileRepository;
import ru.mdm.files.service.event.CreateFileEventSuccess;
import ru.mdm.files.util.ExceptionUtils;
import ru.mdm.kafka.service.KafkaService;

import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final ContentService contentService;
    private final FileRepository fileRepository;
    private final FileMapper mapper;
    private final KafkaService kafkaService;

    @Override
    @Transactional
    public Mono<FileMetadataDto> createFile(UploadFileMetadataDto metadataDto, Flux<DataBuffer> data, boolean compress) {
        return Mono.just(mapper.toEntity(metadataDto))
                .flatMap(file -> contentService.createContent(metadataDto.getFileName(), data, compress)
                        .map(content -> mapper.fillEntity(file, content)))
                .flatMap(fileRepository::save)
                .flatMap(file -> kafkaService.sendEvent(CreateFileEventSuccess.of(file)).thenReturn(file))
                .map(mapper::toDto)
                .onErrorMap(ExceptionUtils.extExceptionMapper(ErrorCode.CANNOT_CREATE_FILE.getText()));
    }

    @Override
    public Mono<ContentWithMetadataDto> getFileContent(UUID fileId) {
        return getFileById(fileId)
                .map(mapper::toContentWithMetadataDto)
                .flatMap(dto -> Mono.fromSupplier(() -> contentService.getContent(dto.getContentRef(), dto.isCompressed()))
                        .doOnNext(dto::setContent)
                        .thenReturn(dto))
                .onErrorMap(ExceptionUtils.extExceptionMapper(ErrorCode.CANNOT_CREATE_FILE.getText()));
    }

    @Override
    public Mono<FileMetadataDto> getFileMetadata(UUID fileId) {
        return getFileById(fileId)
                .map(mapper::toDto)
                .onErrorMap(ExceptionUtils.extExceptionMapper(ErrorCode.CANNOT_GET_FILE_METADATA.getText()));
    }

    @Override
    public Mono<FileMetadataDto> updateFileMetadata(UUID fileId, UploadFileMetadataDto dto) {
        return getFileById(fileId)
                .map(file -> mapper.update(file, dto))
                .flatMap(fileRepository::save)
                .map(mapper::toDto)
                .onErrorMap(ExceptionUtils.extExceptionMapper(ErrorCode.CANNOT_UPDATE_FILE_METADATA.getText()));
    }

    @Override
    public Mono<FileMetadataDto> deleteFile(UUID fileId) {
        return getFileById(fileId)
                .flatMap(file -> fileRepository.deleteById(fileId)
                        .then(contentService.deleteContent(file.getContentRef()))
                        .thenReturn(file))
                .map(mapper::toDto)
                .onErrorMap(ExceptionUtils.extExceptionMapper(ErrorCode.CANNOT_DELETE_FILE.getText()));
    }

    @Override
    public Flux<FileMetadataDto> getFiles(Pageable pageable) {
        return fileRepository.findAllBy(pageable)
                .map(mapper::toDto)
                .onErrorMap(ExceptionUtils.extExceptionMapper(ErrorCode.CANNOT_GET_FILES.getText()));
    }

    private Mono<File> getFileById(UUID fileId) {
        return fileRepository.findById(fileId)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new ResourceNotFoundException(ErrorCode.FILE_NOT_FOUND.buildErrorText(fileId)))));
    }
}
