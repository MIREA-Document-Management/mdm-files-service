package ru.mdm.files.service.event;

import ru.mdm.files.model.entity.File;
import ru.mdm.kafka.model.Event;

/**
 * Событие при успешном создании файла.
 */
public class CreateFileEventSuccess extends Event<File> {

    public static final String EVENT_TYPE = "mdm-files-service.CreateFileEvent.Success";

    public CreateFileEventSuccess() {
        super(EVENT_TYPE);
    }

    public CreateFileEventSuccess(File data) {
        super(EVENT_TYPE, data);
    }

    public static CreateFileEventSuccess of() {
        return new CreateFileEventSuccess();
    }

    public static CreateFileEventSuccess of(File file) {
        return new CreateFileEventSuccess(file);
    }
}
