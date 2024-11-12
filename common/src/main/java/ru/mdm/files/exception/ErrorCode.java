package ru.mdm.files.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CANNOT_WRITE_FILE_TO_STORAGE("Cannot write file to storage"),
    CANNOT_CREATE_FILE("Не удалось создать файл"),
    FILE_NOT_FOUND("Не найден файл с id = %s"),
    UNZIP_ERROR("Ошибка при распаковке данных")
    ;

    private final String text;

    public String buildErrorText(Object... params) {
        return String.format(this.getText(), params);
    }
}
