package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FileStorageType;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto extends EntityDto {
    private String fileName;
    private String fileType;
    private String extension;
    private Boolean isActive;
    private Date lastAccessed;
    private String checksum;
    private Boolean isPublic;
    private FileStorageType fileStorageType;
    private String storagePath;
    private Long fileSize;
}
