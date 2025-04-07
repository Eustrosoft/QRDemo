package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.RegistrationRequest;
import org.eustrosoft.entitites.enums.RegistrationStatus;
import org.eustrosoft.exceptions.CommonException;
import org.eustrosoft.exceptions.JsonApiError;
import org.eustrosoft.repositories.RegistrationRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationRequestStateService {
    private final RegistrationRequestRepository repository;
    private final RegistrationRequestService service;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RegistrationRequest start(Long id) {
        RegistrationRequest registrationRequest = service.findById(id);
        RegistrationStatus status = registrationRequest.getStatus();
        checkIfRequestIsNotClosed(status);
        if (!RegistrationStatus.PENDING.equals(status)) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.CONFLICT, 409L,
                            "exceptions.title.processing_error",
                            "exceptions.detail.registration_not_in_correct_state",
                            new JsonApiError.Source("state"), RegistrationStatus.PENDING.name()
                    )
            );
        }
        repository.changeState(registrationRequest.getId(), RegistrationStatus.IN_WORK);
        return service.findById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RegistrationRequest accept(Long id) {
        RegistrationRequest registrationRequest = service.findById(id);
        RegistrationStatus status = registrationRequest.getStatus();
        checkIfRequestIsNotClosed(status);
        if (!RegistrationStatus.IN_WORK.equals(status)) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.CONFLICT, 409L,
                            "exceptions.title.processing_error",
                            "exceptions.detail.registration_not_in_correct_state",
                            new JsonApiError.Source("state"), RegistrationStatus.IN_WORK.name()
                    )
            );
        }
        repository.changeState(registrationRequest.getId(), RegistrationStatus.ACCEPTED);
        return service.findById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RegistrationRequest reject(Long id) {
        RegistrationRequest registrationRequest = service.findById(id);
        RegistrationStatus status = registrationRequest.getStatus();
        checkIfRequestIsNotClosed(status);
        if (!RegistrationStatus.IN_WORK.equals(status)) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.CONFLICT, 409L,
                            "exceptions.title.processing_error",
                            "exceptions.detail.registration_not_in_correct_state",
                            new JsonApiError.Source("state"), RegistrationStatus.IN_WORK.name()
                    )
            );
        }
        repository.changeState(registrationRequest.getId(), RegistrationStatus.REJECTED);
        return service.findById(id);
    }

    private void checkIfRequestIsNotClosed(RegistrationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status could not be empty");
        }
        if (RegistrationStatus.ACCEPTED.equals(status) || RegistrationStatus.REJECTED.equals(status)) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.CONFLICT, 409L,
                            "exceptions.title.processing_error",
                            "exceptions.detail.registration_is_closed",
                            new JsonApiError.Source("state")
                    )
            );
        }
    }
}
