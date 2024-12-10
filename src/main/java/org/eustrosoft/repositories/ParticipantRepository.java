package org.eustrosoft.repositories;

import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.projections.ParticipantSettingsProjection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    Optional<ParticipantSettingsProjection> findSettingsById(Long id);

    @Modifying
    @Query(value = "update participant set settings = ?2 where id = ?1", nativeQuery = true)
    Integer updateSettings(Long id, String settings);

    <T> Optional<T> findById(Long id, Class<T> type);

    Optional<Participant> findByUsername(String username);

    Optional<Participant> findByEmail(String email);
}
