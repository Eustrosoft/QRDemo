package org.eustrosoft.repositories.projections;

import org.eustrosoft.entitites.enums.FileStorageType;

import java.util.Date;

public interface FileProjection extends EntityProjection {

    String getFileName();

    String getFileType();

    String getExtension();

    String getChecksum();

    Boolean getIsActive();

    Boolean getIsPublic();

    Date getLastAccessed();

    FileStorageType getStoragePlace();

    String getStoragePath();

    Long getFileSize();
}
