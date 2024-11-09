package ru.mdm.files.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;

/**
 * Сервис для работы с файлами.
 */
public interface FileService {

    /**
     * Создать файл.
     *
     * @param metadataDto метаданные файла
     * @param data        поток с бинарными данными контента
     * @param compress    нужно ли сжимать файл
     * @return метаданные созданного файла
     */
    Mono<FileMetadataDto> createFile(@Valid UploadFileMetadataDto metadataDto, @NotNull Flux<DataBuffer> data, boolean compress);
}
