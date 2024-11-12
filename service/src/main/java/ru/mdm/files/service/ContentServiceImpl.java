package ru.mdm.files.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.model.dto.ContentDto;
import ru.mdm.files.repository.ContentRepository;
import ru.mdm.files.service.zip.Compressor;
import ru.mdm.files.service.zip.Decompressor;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса управления контентом.
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    /**
     * Библиотека для получения типа контента.
     */
    private static final Tika tika = new Tika();

    private final ContentRepository contentRepository;

    @Override
    public Mono<ContentDto> createContent(String fileName, Flux<DataBuffer> data, boolean compress) {
        var length = new AtomicLong();
        var actualLength = new AtomicLong();
        var mimeType = new AtomicReference<String>();

        return deflate(data, length, actualLength, mimeType, fileName, compress)
                .transform(dataBufferFlux -> contentRepository.createContent(fileName, dataBufferFlux))
                .last()
                .map(contentRef -> new ContentDto(fileName, contentRef, mimeType.get(), length.get(), actualLength.get(), compress));
    }

    @Override
    public Flux<DataBuffer> getContent(String contentRef, boolean decompress) {
        var content =  contentRepository.getContent(contentRef);
        if (decompress) {
            content = inflate(content);
        }
        return content;
    }

    /**
     * Сжать контент при необходимости и заполнить необходимые параметры.
     *
     * @param data         контент
     * @param length       размер контента в байтах
     * @param actualLength фактический размер контента в байтах (после сжатия)
     * @param mimeType     тип контента
     * @param fileName     имя файла
     * @param compress     нужно ли сжимать контент
     * @return сжатый контент
     */
    private Flux<DataBuffer> deflate(Flux<DataBuffer> data, AtomicLong length, AtomicLong actualLength, AtomicReference<String> mimeType,
                                     String fileName, boolean compress) {
        var compressor = new Compressor(true);
        var endBuffer = DefaultDataBufferFactory.sharedInstance.allocateBuffer(0);
        return data
                .doOnNext(dataBuffer -> {
                    if (length.get() == 0) {
                        mimeType.set(tika.detect(dataBuffer.asByteBuffer().array(), fileName));
                    }
                    length.addAndGet(dataBuffer.readableByteCount());
                })
                .concatWithValues(endBuffer)
                .flatMapSequential(dataBuffer -> {
                    if (compress) {
                        return (dataBuffer.capacity() != 0) ?
                                Mono.just(compressor.deflate(dataBuffer)) :
                                Mono.just(compressor.doFinal());
                    }
                    return (dataBuffer.capacity() == 0) ? Mono.empty() : Mono.just(dataBuffer);
                })
                .doOnNext(dataBuffer -> actualLength.addAndGet(dataBuffer.readableByteCount()))
                .doFinally(signalType -> compressor.close());
    }

    /**
     * Разжать контент файла.
     *
     * @param data контент файла
     * @return разжатый контент
     */
    private Flux<DataBuffer> inflate(Flux<DataBuffer> data) {
        var decompressor = new Decompressor();
        return data
                .map(decompressor::inflate)
                .doFinally(signalType -> decompressor.close());
    }
}
