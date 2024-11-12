package ru.mdm.files.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.mdm.files.model.dto.ContentDto;
import ru.mdm.files.model.dto.ContentWithMetadataDto;
import ru.mdm.files.model.dto.FileMetadataDto;
import ru.mdm.files.model.dto.UploadFileMetadataDto;
import ru.mdm.files.model.entity.File;

@Mapper
public interface FileMapper {

    File toEntity(UploadFileMetadataDto dto);

    File fillEntity(@MappingTarget File file, ContentDto dto);

    FileMetadataDto toDto(File entity);

    ContentWithMetadataDto toContentWithMetadataDto(File entity);
}
