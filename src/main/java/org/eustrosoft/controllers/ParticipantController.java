package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.ParticipantDto;
import org.eustrosoft.dtos.PasswordChangeDto;
import org.eustrosoft.dtos.SettingsChangeDto;
import org.eustrosoft.dtos.SettingsDto;
import org.eustrosoft.mappers.ParticipantMapper;
import org.eustrosoft.services.ParticipantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("Participant")
@RequestMapping("/v1/api/secured/participants")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantMapper mapper;
    private final ParticipantService participantService;

    @GetMapping("/me")
    public ParticipantDto getCurrentUser() throws IllegalAccessException {
        return mapper.toParticipantDto(participantService.getCurrentOrThrow());
    }

    @PostMapping("/settings/change-password")
    public void changePassword(@RequestBody PasswordChangeDto dto) throws IllegalAccessException {
        participantService.changePassword(dto);
    }

    @GetMapping("/settings")
    public SettingsDto getSettings() throws IllegalAccessException {
        return mapper.toSettingsDto(participantService.getSettings());
    }

    @PatchMapping("/settings")
    public SettingsDto updateSettings(@RequestBody SettingsChangeDto dto) throws IllegalAccessException {
        return mapper.toSettingsDto(participantService.updateSettings(dto));
    }
}
