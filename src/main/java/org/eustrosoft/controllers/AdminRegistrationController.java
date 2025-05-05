package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController
@RequestMapping("/v1/api/admin/panel/registrations")
@RequiredArgsConstructor
public class AdminRegistrationController {
    private final RegistrationRequestService service;
    private final RegistrationRequestStateService stateService;
    private final RegistrationRequestMapper mapper;

    @ApiOperation(value = "Get all registration requests")
    @GetMapping
    public List<RegistrationRequestDto> getRegistrationRequests() {
        return mapper.toDtoList(service.findAll());
    }

    @ApiOperation(value = "Get registration request by ID")
    @GetMapping("/{id}")
    public RegistrationRequestDto getRegistrationRequests(@PathVariable("id") Long id) {
        return mapper.toDto(service.findById(id));
    }

    @ApiOperation(value = "Start processing the request")
    @PostMapping("/{id}/start-processing")
    public RegistrationRequestDto startProcessing(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.start(id));
    }

    @ApiOperation(value = "Accept user registration request")
    @PostMapping("/{id}/accept")
    public RegistrationRequestDto accept(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.accept(id));
    }

    @ApiOperation(value = "Reject user registration request")
    @PostMapping("/{id}/reject")
    public RegistrationRequestDto reject(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.reject(id));
    }
}
