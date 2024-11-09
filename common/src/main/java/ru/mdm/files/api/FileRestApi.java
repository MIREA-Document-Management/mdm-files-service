package ru.mdm.files.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;

import java.util.UUID;

/**
 * REST-API для работы с файлами.
 */
public interface FileRestApi {

    String BASE_PATH = "/api/v1/files";

    @Operation(summary = "Создать файл",
            description = "Создать новый файл и разместить его в хранилище",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Файл успешно создан",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = FileMetadataDto.class))),
                    @ApiResponse(responseCode = "400", description = "Неверный формат переданных значений",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true))),
            })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    Mono<FileMetadataDto> createFile(
            @Parameter(description = "Метаданные файла", required = true)
            @RequestPart(name = "metadata") UploadFileMetadataDto metadataDto,
            @Parameter(description = "Содержимое файла", required = true)
            @RequestPart(name = "file") Mono<FilePart> file,
            @Parameter(description = "Сжать файл")
            @RequestParam(required = false, defaultValue = "false") Boolean compress
    );

    @Operation(summary = "Получить контент файла",
            description = "Получить контент файла",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл успешно создан",
                            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Неверный формат переданных значений",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Файл не найден",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true))),
            })
    @GetMapping("/{fileId}")
    Mono<ResponseEntity<Flux<DataBuffer>>> getFileContent(
            @Parameter(description = "Идентификатор файла", required = true)
            @PathVariable UUID fileId
    );
}
