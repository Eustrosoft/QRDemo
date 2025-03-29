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
    @Query(value = "update file set file_name = ?2, " +
            "file_type = ?3, extension = ?4, checksum = ?5, file_size = ?6 where id = ?1",
            nativeQuery = true
    )
    Integer updateFileData(Long id, String fileName, String fileType, String extension, String checksum, Long fileSize);

    @Query(value = "select count(*) from qr_file where file_id = ?1", nativeQuery = true)
    Integer countRelatedQRs(Long id);

    @Query(value = "select count(*) from form_file where file_id = ?1", nativeQuery = true)
    Integer countRelatedForms(Long id);
}
