package org.eustrosoft.mappers;

import org.apache.commons.compress.utils.FileNameUtils;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.FileDto;
import org.eustrosoft.entitites.File;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.utils.ChecksumUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileMapper extends EntityMapper {

    public FileDto toDto(FileProjection fileProjection) {
        if (fileProjection == null) {
            return null;
        }
        FileDto dto = new FileDto(
                fileProjection.getFileName(),
                fileProjection.getFileType(),
                fileProjection.getExtension(),
                fileProjection.getIsActive(),
                fileProjection.getLastAccessed(),
                fileProjection.getChecksum(),
                fileProjection.getIsPublic(),
                fileProjection.getStoragePlace(),
                fileProjection.getStoragePath(),
                fileProjection.getFileSize()
        );
        dto.setId(fileProjection.getId());
        dto.setCreated(fileProjection.getCreated());
        dto.setUpdated(fileProjection.getUpdated());
        dto.setName(fileProjection.getName());
        dto.setDescription(fileProjection.getDescription());
        return dto;
    }

    public List<FileDto> toListDto(List<? extends FileProjection> fileProjections) {
        if (fileProjections == null) {
            return Collections.emptyList();
        }
        return fileProjections.stream().map(this::toDto).collect(Collectors.toList());
    }

    public File toEntity(FileProjection fileProjection) {
        if (fileProjection == null) {
            return null;
        }
        File file = super.toEntity(fileProjection, File.class);
        file.setLastAccessed(fileProjection.getLastAccessed());
        file.setStoragePath(fileProjection.getStoragePath());
        file.setChecksum(fileProjection.getChecksum());
        file.setFileType(fileProjection.getFileType());
        file.setIsPublic(fileProjection.getIsPublic());
        file.setIsActive(fileProjection.getIsActive());
        file.setStoragePlace(fileProjection.getStoragePlace());
        file.setFileSize(fileProjection.getFileSize());
        file.setFileName(fileProjection.getFileName());
        return file;
    }

    public List<File> toListEntity(List<FileProjection> fileProjections) {
        if (fileProjections == null) {
            return Collections.emptyList();
        }
        return fileProjections.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public File toEntityFromDto(FileDto dto) {
        if (dto == null) {
            return null;
        }
        File file = super.toEntity(dto, File.class);
        file.setLastAccessed(dto.getLastAccessed());
        file.setStoragePath(dto.getStoragePath());
        file.setChecksum(dto.getChecksum());
        file.setFileType(dto.getFileType());
        file.setIsPublic(dto.getIsPublic());
        file.setIsActive(dto.getIsActive());
        file.setFileSize(dto.getFileSize());
        file.setFileName(dto.getFileName());
        return file;
    }

    public List<File> toListEntityFromDtos(List<FileDto> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        return dtos.stream().map(this::toEntityFromDto).collect(Collectors.toList());
    }

    public File toEntity(String name, MultipartFile file) throws IOException {
        return toEntity(
                FileUploadRequest.builder()
                        .name(name)
                        .file(file)
                        .build()
        );
    }

    public File toEntity(FileUploadRequest fur) throws IOException {
        if (fur == null) {
            return null;
        }
        MultipartFile file = fur.getFile();
        if (fur.getName() == null || file == null) {
            return null;
        }
        File entity = new File();
        entity.setName(fur.getName());
        entity.setDescription(fur.getDescription());
        entity.setFileSize(file.getSize());
        entity.setExtension(FileNameUtils.getExtension(file.getOriginalFilename()));
        entity.setFileName(file.getOriginalFilename());
        entity.setStoragePlace(fur.getFileStorageType());
        byte[] bytes = file.getBytes();
        entity.setFileData(bytes);
        entity.setChecksum(String.valueOf(ChecksumUtils.getCRC32Checksum(bytes)));
        entity.setIsActive(fur.isActive());
        entity.setIsPublic(fur.isPublic());
        entity.setFileType(file.getContentType());
        return entity;
    }

}
