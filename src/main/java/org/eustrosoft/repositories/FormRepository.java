package org.eustrosoft.repositories;

import org.eustrosoft.entitites.Form;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends CrudRepository<Form, Long> {

    @EntityGraph(attributePaths = { "fields" })
    Optional<Form> findById(Long id);

    @EntityGraph(attributePaths = { "fields" })
    <T> Optional<T> findById(Long id, Class<T> type);

    Iterable<FormSimpleProjection> findAllByParticipantIdOrderByUpdatedDesc(Long participantId);

    @EntityGraph(attributePaths = { "fields" })
    <T> Iterable<T> findAllByParticipantId(Long participantId, Class<T> type);
}
