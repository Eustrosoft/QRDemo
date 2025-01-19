package org.eustrosoft.repositories;

import org.eustrosoft.entitites.File;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends CrudRepository<File, Long> {

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Iterable<T> findAllByParticipantIdOrderByUpdatedDesc(Long participantId, Class<T> type);
}
