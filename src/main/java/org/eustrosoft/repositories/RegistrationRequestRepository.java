package org.eustrosoft.repositories;

import org.eustrosoft.entitites.RegistrationRequest;
import org.eustrosoft.entitites.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface RegistrationRequestRepository extends CrudRepository<RegistrationRequest, Long> {

    <T> Iterable<T> findAllByCreatedBeforeOrderByCreatedDesc(Date before, Class<T> type);

    @Modifying
    @Query(value = "UPDATE RegistrationRequest SET status = :status WHERE id = :id")
    Integer changeState(
            @Param("id") Long id,
            @Param("status") RegistrationStatus status
    );

    // TODO: error with processing UUID from postgresql (type error 1111)
    @Query(
            nativeQuery = true,
            value = "SELECT save_registration_request(?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, CAST(?9 AS INET), ?10, ?11)"
    )
    String saveRegistrationRequest(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String website,
            String organization,
            String country,
            String city,
            String inet,
            String userAgent,
            String referrerUrl
    );

    @Query(nativeQuery = true, value = "SELECT * FROM get_registration_request_details(?1)")
    RegistrationRequestDetails getRegistrationRequestDetails(UUID registrationId);

    interface RegistrationRequestDetails {
        String getUsername();
        RegistrationStatus getStatus();
        String getStatusMsg();
    }
}
