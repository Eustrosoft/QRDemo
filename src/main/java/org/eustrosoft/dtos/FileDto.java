package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FileStorageType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto extends EntityDto {
    @Size(max = 256)
    private String fileName;
    @Size(max = 128)
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
