package org.eustrosoft.repositories;

import org.eustrosoft.entitites.QR;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QRRepository extends CrudRepository<QR, Long> {

    Optional<QR> findByCode(Long code);

    Iterable<QRSimpleProjection> findAllByOrderByUpdated();

    Iterable<QRSimpleProjection> findAllByParticipantIdOrderByUpdatedDesc(Long participantId);
}
