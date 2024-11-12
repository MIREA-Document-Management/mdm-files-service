package ru.mdm.files.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Модель метаданных для создания/обновления файла")
public class UploadFileMetadataDto {

    @Schema(description = "Имя файла", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Отсутствует имя файла")
    private String fileName;

    @Schema(description = "Дополнительная информация о файле")
    private Map<String, Object> data;
}
