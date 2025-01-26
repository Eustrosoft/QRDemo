package org.eustrosoft.repositories;

import org.eustrosoft.entitites.File;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends CrudRepository<File, Long> {

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Iterable<T> findAllByParticipantIdOrderByUpdatedDesc(Long participantId, Class<T> type);

    @Modifying
    @Query(value = "update file set file_data = ?2, file_name = ?3, " +
            "file_type = ?4, extension = ?5, checksum = ?6, file_size = ?7 where id = ?1",
            nativeQuery = true
    )
    Integer updateFileData(Long id, byte[] file_data, String fileName, String fileType, String extension, String checksum, Long fileSize);
}
