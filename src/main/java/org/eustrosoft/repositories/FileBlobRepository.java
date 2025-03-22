package org.eustrosoft.repositories;

import org.eustrosoft.entitites.FileBlob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.io.OutputStream;

@Repository
public interface FileBlobRepository extends CrudRepository<FileBlob, Long> {

    FileBlob getByZOIDAndZRID(Long ZOID, Long ZRID);
}
