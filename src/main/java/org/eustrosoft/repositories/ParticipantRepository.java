package org.eustrosoft.repositories;

import org.eustrosoft.entitites.Participant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    @Modifying
    @Query(value = "update participant set settings = ?2 where id = ?1", nativeQuery = true)
    Integer updateSettings(Long id, String settings);

    @EntityGraph(attributePaths = { "roles" })
    <T> Optional<T> findById(Long id, Class<T> type);

    @EntityGraph(attributePaths = { "roles" })
    Optional<Participant> findByUsername(String username);

    @EntityGraph(attributePaths = { "roles" })
    <T> Optional<T> findByUsername(String username, Class<T> type);

    Optional<Participant> findByEmail(String email);
}
