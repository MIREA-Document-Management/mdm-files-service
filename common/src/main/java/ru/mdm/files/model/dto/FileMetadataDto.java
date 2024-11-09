package ru.mdm.files.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Schema(description = "Метаданные сохраненного файла")
public class FileMetadataDto {

    @Schema(description = "Идентификатор файла")
    private UUID id;

    @Schema(description = "Имя файла")
    private String fileName;

    @Schema(description = "Идентификатор контента")
    private String contentRef;

    @Schema(description = "Тип контента файла")
    private String mimeType;

    @Schema(description = "Исходный размер контента файла в байтах")
    private Long fileSize;

    @Schema(description = "Фактический размер файла после сжатия/шифрования в байтах")
    private Long actualFileSize;

    @Schema(description = "Признак сжатия файла")
    @JsonProperty("isCompressed")
    private boolean compressed;

    @Schema(description = "Кто создал файл")
    private String createdBy;

    @Schema(description = "Дата и время создания")
    private LocalDateTime creationDate;

    @Schema(description = "Кто последний изменил файл")
    private String modifiedBy;

    @Schema(description = "Дата и время последнего изменения")
    private LocalDateTime modificationDate;

    @Schema(description = "Дополнительная информация о файле")
    private Map<String, Object> data;
}
