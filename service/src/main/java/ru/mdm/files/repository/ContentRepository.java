package ru.mdm.files.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Репозиторий для взаимодействия с контентом файлов.
 */
public interface ContentRepository {

    /**
     * Сохранить контент файла в хранилище.
     *
     * @param fileName имя файла
     * @param data     контент файла
     * @return путь к сохраненному контенту
     */
    Mono<String> createContent(@NotNull String fileName, @NotNull Flux<DataBuffer> data);
}