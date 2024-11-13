package ru.mdm.files.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CANNOT_WRITE_FILE_TO_STORAGE("Не удалось сохранить файл в хранилище"),
    CANNOT_CREATE_FILE("Не удалось создать файл"),
    FILE_NOT_FOUND("Не найден файл с id = %s"),
    UNZIP_ERROR("Ошибка при распаковке данных"),
    CANNOT_GET_FILE_METADATA("Не удалось получить метаданные файла"),
    CANNOT_UPDATE_FILE_METADATA("Не удалось обновить метаданные файла"),
    CANNOT_GET_FILES("Не удалось получить список метаданных файлов"),
    CANNOT_DELETE_FILE("Не удалось удалить файл"),
    CANNOT_DELETE_FILE_FROM_STORAGE("Не удалось удалить файл из хранилища")
    ;

    private final String text;

    public String buildErrorText(Object... params) {
        return String.format(this.getText(), params);
    }
}
