package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController
@RequestMapping("/v1/api/unsecured/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationRequestService registrationRequestService;
    private final RegistrationRequestMapper mapper;

    @ApiOperation(value = "Get status for registration request by UUID")
    @GetMapping("/{id}")
    public RegistrationRequestRepository.RegistrationRequestDetails getRegistrationRequestStatus(
            @PathVariable("id") UUID registrationIdentifier
    ) {
        return registrationRequestService.getRegistrationRequestDetails(registrationIdentifier);
    }

    @ApiOperation(value = "Create new registration request")
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
