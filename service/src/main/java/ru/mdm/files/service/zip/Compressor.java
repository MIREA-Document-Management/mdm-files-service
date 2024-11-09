package ru.mdm.files.service.zip;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;

/**
 * Компрессор для сжатия данных.
 */
public class Compressor {

    private final Deflater deflater = new Deflater(Deflater.BEST_SPEED, true);

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);

    private final byte[] transfer = new byte[1024];

    private final int flushMode;

    public Compressor(boolean syncFlush) {
        flushMode = syncFlush ? Deflater.SYNC_FLUSH : Deflater.NO_FLUSH;
    }

    /**
     * Сжать блок данных.
     *
     * @param in блок данных для сжатия
     * @return сжатый блок
     */
    public DataBuffer deflate(DataBuffer in) {
        deflater.setInput(in.asByteBuffer());

        while (!deflater.needsInput()) {
            int r = deflater.deflate(transfer, 0, transfer.length, flushMode);
            buffer.write(transfer, 0, r);
        }

        byte[] outBytes = buffer.toByteArray();
        buffer.reset();
        return DefaultDataBufferFactory.sharedInstance.wrap(outBytes);
    }

    /**
     * Получить финальный буфер из оставшихся данных.
     *
     * @return буфер
     */
    public DataBuffer doFinal() {
        deflater.finish();

        int r;
        do {
            r = deflater.deflate(transfer, 0, transfer.length, Deflater.FULL_FLUSH);
            buffer.write(transfer, 0, r);
        } while (r == transfer.length);

        deflater.reset();

        byte[] outBytes = buffer.toByteArray();
        buffer.reset();
        return DefaultDataBufferFactory.sharedInstance.wrap(outBytes);
    }

    /**
     * Закрыть компрессор.
     */
    public void close() {
        deflater.end();
    }
}

