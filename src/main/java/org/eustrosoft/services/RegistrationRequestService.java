package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.RegistrationRequest;
import org.eustrosoft.exceptions.CommonException;
import org.eustrosoft.exceptions.JsonApiError;
import org.eustrosoft.repositories.RegistrationRequestRepository;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationRequestService {
    private final RegistrationRequestRepository repository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<RegistrationRequest> findAll() {
        return CommonUtils.iterableToList(
                repository.findAllByCreatedBeforeOrderByCreatedDesc(
                        new Date(),
                        RegistrationRequest.class
                )
        );
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RegistrationRequest findById(Long id) {
        return repository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public RegistrationRequestRepository.RegistrationRequestDetails getRegistrationRequestDetails(
            UUID requestIdentifier
    ) {
        RegistrationRequestRepository.RegistrationRequestDetails registrationRequestDetails
                = repository.getRegistrationRequestDetails(requestIdentifier);
        if (registrationRequestDetails == null) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, 1008L,
                            "exceptions.title.arguments_not_valid",
                            "exceptions.detail.registration_identifier_not_found",
                            new JsonApiError.Source("request_identifier")
                    )
            );
        }
        return registrationRequestDetails;
    }

    public UUID saveRegistrationRequest(RegistrationRequest registrationRequest) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        try {
            return UUID.fromString(repository.saveRegistrationRequest(
                    registrationRequest.getFirstName(),
                    registrationRequest.getLastName(),
                    registrationRequest.getEmail(),
                    registrationRequest.getPhoneNumber(),
                    registrationRequest.getWebsite(),
                    registrationRequest.getOrganization(),
                    registrationRequest.getCountry(),
                    registrationRequest.getCity(),
                    request.getRemoteAddr(),
                    request.getHeader(HttpHeaders.USER_AGENT),
                    request.getHeader(HttpHeaders.REFERER)
            ));
        } catch (DataIntegrityViolationException ex) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.CONFLICT,
                            "exceptions.title.registration_error",
                            "exceptions.detail.registration_username_or_email_already_exists"
                    )
            );
        }
    }
}
