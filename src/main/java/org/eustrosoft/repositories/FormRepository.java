package org.eustrosoft.repositories;

import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends CrudRepository<Form, Long> {

    Iterable<FormSimpleProjection> findAllByOrderByUpdatedDesc();

    Iterable<FormSimpleProjection> findAllByParticipantOrderByUpdated(Participant participant);
}
