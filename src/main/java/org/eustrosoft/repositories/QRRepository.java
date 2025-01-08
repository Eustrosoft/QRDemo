package org.eustrosoft.repositories;

import org.eustrosoft.entitites.QR;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QRRepository extends CrudRepository<QR, Long> {

    @Modifying
    @Query(value = "update QR q set q.form = null where id IN ?1")
    void annulForm(List<Long> id);

    @Modifying
    @Query(value = "insert into qr_file (qr_id, file_id) values (?1, ?2)", nativeQuery = true)
    void insertFile(Long id, Long fileId);

    @Modifying
    @Query(value = "delete from qr_file qf where qf.qr_id = ?1 and qf.file_id = ?2", nativeQuery = true)
    void deleteFile(Long id, Long fileId);

    <T> Iterable<T> findAllByParticipantIdAndFormId(Long participantId, Long formId, Class<T> clazz);

    @Query("select q from QR q left outer join fetch q.form f where q.id = ?1")
    <T> Optional<T> findById(Long id, Class<T> clazz);

    @Query("select q from QR q left outer join fetch q.form f where q.code = ?1")
    <T> Optional<T> findByCode(Long code, Class<T> clazz);

    @Query(value = "select q from QR q left outer join fetch Form f on q.form=f.id where q.participantId = ?1 order by q.code desc")
    Iterable<QRSimpleProjection> findAllByParticipantIdOrderByCodeDesc(Long participantId);
}
