package ru.mdm.files.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import ru.mdm.files.model.entity.File;

import java.util.UUID;

/**
 * Репозиторий для работы с таблицей файлов в БД.
 */
@Repository
public interface FileRepository extends ReactiveSortingRepository<File, UUID>, ReactiveCrudRepository<File, UUID> {
}
