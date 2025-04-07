package org.eustrosoft.controllers;

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

@RestController
@RequestMapping("/v1/api/admin/panel/registrations")
@RequiredArgsConstructor
public class AdminRegistrationController {
    private final RegistrationRequestService service;
    private final RegistrationRequestStateService stateService;
    private final RegistrationRequestMapper mapper;

    @GetMapping
    public List<RegistrationRequestDto> getRegistrationRequests() {
        return mapper.toDtoList(service.findAll());
    }

    @GetMapping("/{id}")
    public RegistrationRequestDto getRegistrationRequests(@PathVariable("id") Long id) {
        return mapper.toDto(service.findById(id));
    }

    @PostMapping("/{id}/start-processing")
    public RegistrationRequestDto startProcessing(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.start(id));
    }

    @PostMapping("/{id}/accept")
    public RegistrationRequestDto accept(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.accept(id));
    }

    @PostMapping("/{id}/reject")
    public RegistrationRequestDto reject(@PathVariable("id") Long id) {
        return mapper.toDto(stateService.reject(id));
    }
}
