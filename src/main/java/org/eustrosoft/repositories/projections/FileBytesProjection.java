package org.eustrosoft.repositories.projections;

public interface FileBytesProjection extends EntityProjection {

    byte[] getFileData();

    Boolean getIsPublic();
}
