package ru.mdm.files.model.entity;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Сущность для метаданных файла.
 */
@Data
@Table("mdm_files")
public class File {

    /**
     * Идентификатор файла.
     */
    @Id
    @Column("id")
    private UUID id;

    /**
     * Имя файла.
     */
    @Column("file_name")
    private String fileName;

    /**
     * Идентификатор контента.
     */
    @Column("content_ref")
    private String contentRef;

    /**
     * Тип контента файла.
     */
    @Column("mime_type")
    private String mimeType;

    /**
     * Исходный размер контента файла в байтах.
     */
    @Column("file_size")
    private Long fileSize;

    /**
     * Фактический размер файла после сжатия/шифрования в байтах.
     */
    @Column("actual_file_size")
    private Long actualFileSize;

    /**
     * Признак сжатия файла.
     */
    @Column("compressed")
    private boolean compressed;

    /**
     * Кто создал файл.
     */
    @CreatedBy
    @Column("created_by")
    private String createdBy;

    /**
     * Дата и время создания.
     */
    @CreatedDate
    @Column("creation_date")
    private LocalDateTime creationDate;

    /**
     * Кто последний изменил файл.
     */
    @LastModifiedBy
    @Column("modified_by")
    private String modifiedBy;

    /**
     * Дата и время последнего изменения.
     */
    @LastModifiedDate
    @Column("modification_date")
    private LocalDateTime modificationDate;

    /**
     * Дополнительная информация о файле.
     */
    @Column("data")
    private Map<String, Object> data;
}
