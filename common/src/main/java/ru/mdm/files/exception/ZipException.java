package ru.mdm.files.exception;

/**
 * Исключении при сжатии/распаковке
 */
public class ZipException extends RuntimeException {

    public ZipException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getText(), cause);
    }
}
