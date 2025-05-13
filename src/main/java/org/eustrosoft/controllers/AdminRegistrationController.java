package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.RegistrationRequestDto;
import org.eustrosoft.mappers.RegistrationRequestMapper;
import org.eustrosoft.services.RegistrationRequestService;
import org.eustrosoft.services.RegistrationRequestStateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController
@RequestMapping("/v1/api/admin/panel/registrations")
@RequiredArgsConstructor
@Tag(name = "Registration API for admins")
public class AdminRegistrationController {
    private final RegistrationRequestService service;
    private final RegistrationRequestStateService stateService;
    private final RegistrationRequestMapper mapper;

    @Operation(summary = "Get all registration requests")
    @GetMapping
    public List<RegistrationRequestDto> getRegistrationRequests() {
        return mapper.toDtoList(service.findAll());
    }

    @Operation(summary = "Get registration request by ID")
    @GetMapping("/{id}")
    public RegistrationRequestDto getRegistrationRequests(@PathVariable("id") Long id) {
        return mapper.toDto(service.findById(id));
    }

    @Operation(summary = "Start processing the request")
    @PostMapping("/{id}/start-processing")
    public RegistrationRequestDto startProcessing(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.start(id));
    }

    @Operation(summary = "Accept user registration request")
    @PostMapping("/{id}/accept")
    public RegistrationRequestDto accept(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.accept(id));
    }

    @Operation(summary = "Reject user registration request")
    @PostMapping("/{id}/reject")
    public RegistrationRequestDto reject(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.reject(id));
    }
}
