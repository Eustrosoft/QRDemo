package org.eustrosoft.repositories;

import org.eustrosoft.entitites.GSLabel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GSLabelRepository extends CrudRepository<GSLabel, Long> {
    List<GSLabel> findAllByParticipantIdOrderByCreatedDesc(Long participantId);
}
