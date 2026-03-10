package org.eustrosoft.repositories;

import org.eustrosoft.entitites.PCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PCodeRepository extends CrudRepository<PCode, Long> {

    List<PCode> findAllByParticipantId(Long participantId);

    List<PCode> findAllByParticipantIdAndDocId(Long participantId, Long docId);

    Optional<PCode> findByDocId(Long docId);

    Optional<PCode> findByDocIdAndRowId(Long docId, Long rowId);

    void deleteByDocId(Long docId);

    void deleteByDocIdAndRowId(Long docId, Long rowId);
}
