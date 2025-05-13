package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController
@RequestMapping("/v1/api/unsecured/registrations")
@RequiredArgsConstructor
@Tag(name = "Registration API")
public class RegistrationController {
    private final RegistrationRequestService registrationRequestService;
    private final RegistrationRequestMapper mapper;

    @Operation(summary = "Get status for registration request by UUID")
    @GetMapping("/{id}")
    public RegistrationRequestRepository.RegistrationRequestDetails getRegistrationRequestStatus(
            @PathVariable("id") UUID registrationIdentifier
    ) {
        return registrationRequestService.getRegistrationRequestDetails(registrationIdentifier);
    }

    @Operation(summary = "Create new registration request")
    @PostMapping
    public UUID saveRegistrationRequest(@Valid @RequestBody RegistrationRequestCreationDto dto) {
        if (dto == null || dto.hasEmptyFields()) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.parameter_not_provided",
                            "exceptions.detail.username_not_provided",
                            new JsonApiError.Source("firstName, lastName, email, phoneNumber, organization")
                    )
            );
        }

        return registrationRequestService.saveRegistrationRequest(mapper.toModel(dto));
    }
}
