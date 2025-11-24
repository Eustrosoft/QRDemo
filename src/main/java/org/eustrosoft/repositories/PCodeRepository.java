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

    void deleteByDocId(Long docId);
}
