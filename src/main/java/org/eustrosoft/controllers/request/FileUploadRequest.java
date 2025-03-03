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
public class FileUploadRequest extends EntityDto {
    @Size(max = 128)
    private String name;
    @Size(max = 512)
    private String description;
    private MultipartFile file;
    private FileStorageType fileStorageType = FileStorageType.DB;
    private boolean isPublic = false;
    private boolean isActive = true;
}
