package ru.mdm.files.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о контенте.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDto {

    /**
     * Имя файла.
     */
    private String fileName;

    /**
     * Идентификатор контента.
     */
    private String contentRef;

    /**
     * Тип контента файла.
     */
    private String mimeType;

    /**
     * Исходный размер контента файла в байтах.
     */
    private Long fileSize;

    /**
     * Фактический размер файла после сжатия/шифрования в байтах.
     */
    private Long actualFileSize;

    /**
     * Признак сжатия файла.
     */
    private boolean compressed;
}
