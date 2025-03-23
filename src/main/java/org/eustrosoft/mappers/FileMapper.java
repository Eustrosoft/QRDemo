package org.eustrosoft.mappers;

import lombok.SneakyThrows;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.eustrosoft.controllers.request.FileReUploadRequest;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.controllers.request.FileWithBlobUploadRequest;
import org.eustrosoft.dtos.FileChangeDto;
import org.eustrosoft.dtos.FileDto;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.FileBlob;
import org.eustrosoft.entitites.subentities.FileData;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.utils.ChecksumUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        if (file == null) {
            return null;
        }
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
        if (file == null) {
            return null;
        }
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
                new FileUploadRequest(URLDecoder.decode(name, StandardCharsets.UTF_8.name()), file)
        );
    }

    @SneakyThrows
    public File toEntity(FileUploadRequest fur) throws IOException {
        if (fur == null) {
            return null;
        }
        MultipartFile file = fur.getFile();
        if (file == null && StringUtils.isBlank(fur.getStoragePath())) {
            return null;
        }
        File entity = new File();
        entity.setName(URLDecoder.decode(fur.getName(), StandardCharsets.UTF_8.name()));
        entity.setDescription(fur.getDescription());
        if (file != null) {
            entity.setFileSize(file.getSize());
            entity.setExtension(FileNameUtils.getExtension(file.getOriginalFilename()));
            entity.setFileName(file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            entity.setChecksum(String.valueOf(ChecksumUtils.getCRC32Checksum(bytes)));
            entity.setFileType(file.getContentType());
        }
        entity.setStoragePlace(fur.getFileStorageType());
        entity.setStoragePath(fur.getStoragePath());
        entity.setIsActive(fur.isActive());
        entity.setIsPublic(fur.isPublic());
        return entity;
    }

    @SneakyThrows
    public Map.Entry<File, FileBlob> toFileMap(FileWithBlobUploadRequest fur) {
        if (fur == null) {
            return null;
        }
        MultipartFile multipartFile = fur.getChunk();
        if (multipartFile == null) {
            throw new IllegalArgumentException("File bytes not found");
        }
        File file = new File();
        file.setName(URLDecoder.decode(fur.getName(), StandardCharsets.UTF_8.name()));
        file.setDescription(fur.getDescription());
        if (fur.getFileId() != null) {
            file.setId(fur.getFileId());
        }
        file.setFileSize(fur.getFileSize());
        file.setExtension(FileNameUtils.getExtension(multipartFile.getOriginalFilename()));
        file.setFileName(multipartFile.getOriginalFilename());
        file.setIsActive(fur.isActive());
        file.setIsPublic(fur.isPublic());
        file.setStoragePlace(fur.getFileStorageType());
        file.setStoragePath(fur.getStoragePath());

        byte[] bytes = multipartFile.getBytes();

        file.setChecksum(String.valueOf(ChecksumUtils.getCRC32Checksum(bytes)));
        file.setFileType(URLConnection.guessContentTypeFromName(multipartFile.getOriginalFilename()));

        FileBlob blob = new FileBlob();
        blob.setNo(fur.getNo());
        blob.setChunk(bytes);
        blob.setCrc32(ChecksumUtils.getCRC32Checksum(bytes));
        blob.setSize(multipartFile.getSize());

        return new AbstractMap.SimpleEntry<>(file, blob);
    }

    @SneakyThrows
    public File toEntity(FileReUploadRequest fur) throws IOException {
        if (fur == null) {
            return null;
        }
        MultipartFile file = fur.getFile();
        if (file == null) {
            return null;
        }
        File entity = new File();
        entity.setFileSize(file.getSize());
        entity.setExtension(FileNameUtils.getExtension(file.getOriginalFilename()));
        entity.setFileName(file.getOriginalFilename());
        byte[] bytes = file.getBytes();
        entity.setChecksum(String.valueOf(ChecksumUtils.getCRC32Checksum(bytes)));
        entity.setFileType(file.getContentType());
        return entity;
    }

    public FileData toEntity(FileChangeDto dto) {
        if (dto == null) {
            return null;
        }
        FileData fd = super.toEntity(dto, FileData.class);
        if (fd == null) {
            return null;
        }
        if (dto.getStoragePath() != null) {
            fd.setStoragePath(dto.getStoragePath());
        }
        fd.setIsActive(dto.getIsActive());
        fd.setIsPublic(dto.getIsPublic());
        return fd;
    }

}
