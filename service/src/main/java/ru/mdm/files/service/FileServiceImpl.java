package ru.mdm.files.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.exception.ErrorCode;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;
import ru.mdm.files.model.mapper.FileMapper;
import ru.mdm.files.repository.FileRepository;
import ru.mdm.files.util.ExceptionUtils;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final ContentService contentService;
    private final FileRepository fileRepository;
    private final FileMapper mapper;

    @Override
    public Mono<FileMetadataDto> createFile(UploadFileMetadataDto metadataDto, Flux<DataBuffer> data, boolean compress) {
        return Mono.just(mapper.toEntity(metadataDto))
                .flatMap(file -> contentService.createContent(metadataDto.getFileName(), data, compress)
                        .map(content -> mapper.fillEntity(file, content)))
                .flatMap(fileRepository::save)
                .map(mapper::toDto)
                .onErrorMap(ExceptionUtils.extExceptionMapper(ErrorCode.CANNOT_CREATE_FILE.getText()));
    }
}
