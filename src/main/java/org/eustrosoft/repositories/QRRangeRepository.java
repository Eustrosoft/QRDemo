package org.eustrosoft.repositories;

import org.eustrosoft.entitites.QRRange;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QRRangeRepository extends CrudRepository<QRRange, Long> {

    Optional<QRRange> findByFrom(Long from);

    Optional<QRRange> findByTo(Long to);

    Optional<QRRange> findByFromOrTo(Long from, Long to);
}
