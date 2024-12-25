package org.eustrosoft.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FileStorageType;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    private String name;
    private String description;
    private MultipartFile file;
    private FileStorageType fileStorageType = FileStorageType.DB;
    private boolean isPublic = false;
    private boolean isActive = true;
}
