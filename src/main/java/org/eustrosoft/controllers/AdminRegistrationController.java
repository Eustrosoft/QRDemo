package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.RegistrationRequestDto;
import org.eustrosoft.mappers.RegistrationRequestMapper;
import org.eustrosoft.services.RegistrationRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/admin/panel/registrations")
@RequiredArgsConstructor
public class AdminRegistrationController {
    private final RegistrationRequestService registrationRequestService;
    private final RegistrationRequestMapper mapper;

    @GetMapping
    public List<RegistrationRequestDto> getRegistrationRequests() {
        return mapper.toDtoList(registrationRequestService.getAllRegistrationRequests());
    }
}
