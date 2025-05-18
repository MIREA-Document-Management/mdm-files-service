package ru.mdm.files.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.client.feign.FilesServiceFeignClient;
import ru.mdm.files.model.dto.ContentWithMetadataDto;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;
import ru.mdm.files.service.FileService;

import java.util.UUID;

/**
 * Реализация клиента для работы с файлами.
 */
@Service
@RequiredArgsConstructor
public class FilesClient implements FileService {

    private final FilesServiceFeignClient filesServiceFeignClient;

    @Override
    public Mono<FileMetadataDto> createFile(UploadFileMetadataDto metadataDto, Flux<DataBuffer> data, boolean compress) {
        return null;
    }

    @Override
    public Mono<ContentWithMetadataDto> getFileContent(UUID fileId) {
        return filesServiceFeignClient.getFileContent(fileId)
                .map(responseEntity -> {
                    var responseDto = new ContentWithMetadataDto();
                    responseDto.setContent(responseEntity.getBody());
                    return responseDto;
                });
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
}
