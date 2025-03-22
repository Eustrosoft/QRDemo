package org.eustrosoft.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.dtos.EntityDto;
import org.eustrosoft.entitites.enums.FileStorageType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileWithBlobUploadRequest extends EntityDto {
    private Long id;
    private Long no;
    private Long total;
    private MultipartFile chunk;
    private Long fileSize;
    @Size(max = 128)
    private String name;
    @Size(max = 512)
    private String description;
    @Size(max = 256)
    private String storagePath;

    private FileStorageType fileStorageType = FileStorageType.DB;
    private boolean isPublic = false;
    private boolean isActive = true;
}
