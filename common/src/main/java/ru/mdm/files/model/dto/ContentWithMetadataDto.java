package ru.mdm.files.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

/**
 * Модель метаданных и бинарного контента для файла
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentWithMetadataDto extends FileMetadataDto {

    /**
     * Бинарный контент файла.
     */
    private Flux<DataBuffer> content;
}
