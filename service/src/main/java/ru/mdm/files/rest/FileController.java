package ru.mdm.files.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.mdm.files.api.FileRestApi;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;
import ru.mdm.files.service.FileService;

/**
 * REST-контроллер для работы с файлами.
 */
@RestController
@RequestMapping(FileRestApi.BASE_PATH)
@RequiredArgsConstructor
public class FileController implements FileRestApi {

    private final FileService fileServiceImpl;

    @Override
    public Mono<FileMetadataDto> createFile(UploadFileMetadataDto metadataDto, Mono<FilePart> file, boolean compress) {
        return Mono.usingWhen(
                file,
                filePart -> fileServiceImpl.createFile(metadataDto, filePart.content(), compress),
                Part::delete
        );
    }
}
