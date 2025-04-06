package org.eustrosoft.repositories;

import org.eustrosoft.entitites.RegistrationRequest;
import org.eustrosoft.entitites.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface RegistrationRequestRepository extends CrudRepository<RegistrationRequest, Long> {

    <T> Iterable<T> findAllByCreatedBeforeOrderByCreatedDesc(Date before, Class<T> type);

    // TODO: error with processing UUID from postgresql (type error 1111)
    @Query(nativeQuery = true, value = "SELECT save_registration_request(?1, ?2, ?3, CAST(?4 AS INET), ?5, ?6)")
    String saveRegistrationRequest(
            String username,
            String password,
            String email,
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
