package org.eustrosoft.repositories;

import org.eustrosoft.entitites.GSLabel;
import org.eustrosoft.entitites.MRange;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MRangeRepository extends CrudRepository<MRange, Long> {
    List<MRange> findAllByParticipantIdOrderByCreatedDesc(Long participantId);
}
