package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.RegistrationRequestCreationDto;
import org.eustrosoft.exceptions.CommonException;
import org.eustrosoft.exceptions.JsonApiError;
import org.eustrosoft.mappers.RegistrationRequestMapper;
import org.eustrosoft.repositories.RegistrationRequestRepository;
import org.eustrosoft.services.RegistrationRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/unsecured/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationRequestService registrationRequestService;
    private final RegistrationRequestMapper mapper;

    @GetMapping("/{id}")
    public RegistrationRequestRepository.RegistrationRequestDetails getRegistrationRequestStatus(
            @PathVariable("id") UUID registrationIdentifier
    ) {
        return registrationRequestService.getRegistrationRequestDetails(registrationIdentifier);
    }

    @PostMapping
    public UUID saveRegistrationRequest(@Valid @RequestBody RegistrationRequestCreationDto dto) {
        if (dto == null || dto.hasEmptyFields()) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.parameter_not_provided",
                            "exceptions.detail.username_not_provided",
                            new JsonApiError.Source("username, email, password, rePassword")
                    )
            );
        }
        if (!dto.getPassword().equals(dto.getRePassword())) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.passwords_are_not_same",
                            "exceptions.detail.passwords_are_not_same",
                            new JsonApiError.Source("password")
                    )
            );
        }

        return registrationRequestService.saveRegistrationRequest(mapper.toModel(dto));
    }
}
