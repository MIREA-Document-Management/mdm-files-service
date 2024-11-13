package ru.mdm.files.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.model.dto.ContentWithMetadataDto;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;

import java.util.UUID;

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

    /**
     * Получить контент и метаданные файла.
     *
     * @param fileId идентификатор файла
     * @return модель с контентом и метаданными файла
     */
    Mono<ContentWithMetadataDto> getFileContent(@NotNull UUID fileId);

    /**
     * Получить метаданные файла.
     *
     * @param fileId идентификатор файла
     * @return метаданные файла
     */
    Mono<FileMetadataDto> getFileMetadata(@NotNull UUID fileId);

    /**
     * Обновить метаданные файла.
     *
     * @param fileId идентификатор файла
     * @param dto модель для обновления метаданных
     * @return обновленные метаданные файла
     */
    Mono<FileMetadataDto> updateFileMetadata(@NotNull UUID fileId, @Valid UploadFileMetadataDto dto);

    /**
     * Удалить файл из хранилища.
     *
     * @param fileId идентификатор файла
     * @return метаданные удаленного файла
     */
    Mono<FileMetadataDto> deleteFile(@NotNull UUID fileId);

    /**
     * Получить список метаданных файлов
     *
     * @param pageable параметры пагинации
     * @return список с метаданными
     */
    Flux<FileMetadataDto> getFiles(Pageable pageable);
}
