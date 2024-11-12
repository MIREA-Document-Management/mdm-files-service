package ru.mdm.files.service.zip;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import ru.mdm.files.exception.ErrorCode;
import ru.mdm.files.exception.ServerException;
import ru.mdm.files.exception.ZipException;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Декомпрессор для распаковки данных.
 */
public class Decompressor {
    private static final Long FIVE_SECONDS= 5_000L;

    private final Inflater inflater = new Inflater(true);

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);

    private final byte[] transfer = new byte[1024];

    /**
     * Распаковать буфер данных.
     *
     * @param in буфер для распаковки
     * @return распакованный буфер
     */
    public DataBuffer inflate(DataBuffer in) {
        inflater.setInput(in.asByteBuffer());

        long currentTimeMillis = System.currentTimeMillis();

        while (!inflater.needsInput()) {
            if ((currentTimeMillis + FIVE_SECONDS) < System.currentTimeMillis()) {
                throw new ServerException("Decompressor.inflate(DataBuffer in) failed with timeout");
            }
            int r;
            try {
                r = inflater.inflate(transfer, 0, transfer.length);
            } catch (DataFormatException e) {
                throw new ZipException(ErrorCode.UNZIP_ERROR, e);
            }
            buffer.write(transfer, 0, r);
        }

        byte[] outBytes = buffer.toByteArray();
        buffer.reset();
        return DefaultDataBufferFactory.sharedInstance.wrap(outBytes);
    }

    /**
     * Закрыть декомпрессор.
     */
    public void close() {
        inflater.end();
    }
}
