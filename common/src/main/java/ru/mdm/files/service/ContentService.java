package ru.mdm.files.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.model.dto.ContentDto;

/**
 * Сервис для работы с контентом файлов.
 */
public interface ContentService {

    /**
     * Сохранить контент в хранилище
     *
     * @param fileName имя файла
     * @param data     поток с бинарными данными контента
     * @param compress нужно ли сжимать контент
     * @return информация о сохраненном контенте
     */
    Mono<ContentDto> createContent(@NotBlank String fileName, @NotNull Flux<DataBuffer> data, boolean compress);

    /**
     * Получить контент файла.
     *
     * @param contentRef ссылка на контент в хранилище
     * @param decompress нужно ли разжать контент
     * @return контент
     */
    Flux<DataBuffer> getContent(@NotNull String contentRef, boolean decompress);
}
