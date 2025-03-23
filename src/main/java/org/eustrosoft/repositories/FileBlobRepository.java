package org.eustrosoft.repositories;

import org.eustrosoft.entitites.FileBlob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

@Repository
public interface FileBlobRepository extends CrudRepository<FileBlob, Long> {

    // FileBlob getByZOIDAndNo(Long ZOID, Long no);

    @Query(value = "select * from file_blob where ZOID = ?1 and no = ?2", nativeQuery = true)
    BlobResponse getByZOIDAndNo(Long ZOID, Long no);

    interface BlobResponse {
        Long getZOID();
        Long getZRID();
        Long getZVER();
        Long getZTOV();
        Long getZSID();
        Short getZLVL();
        Long getZPID();
        Long getZUID();
        Character getZSTA();
        Timestamp getZDATE();
        Timestamp getZDATO();
        Long getZUIDO();
        byte[] getChunk();
        Long getNo();
        Long getSize();
        Long getCrc32();
    }
}
