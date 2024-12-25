package org.eustrosoft.repositories;

import org.eustrosoft.entitites.Form;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends CrudRepository<Form, Long> {

    <T> Optional<T> findById(Long id, Class<T> type);

    Iterable<FormSimpleProjection> findAllByOrderByUpdatedDesc();

    Iterable<FormSimpleProjection> findAllByParticipantIdOrderByUpdatedDesc(Long participantId);
}
