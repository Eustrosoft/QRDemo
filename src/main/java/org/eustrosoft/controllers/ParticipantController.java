package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.ParticipantDto;
import org.eustrosoft.dtos.PasswordChangeDto;
import org.eustrosoft.dtos.SettingsChangeDto;
import org.eustrosoft.dtos.SettingsDto;
import org.eustrosoft.mappers.ParticipantMapper;
import org.eustrosoft.services.ParticipantService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@Validated
@RestController("Participant")
@RequestMapping("/v1/api/secured/participants")
@RequiredArgsConstructor
@Tag(name = "Participants API")
public class ParticipantController {
    private final ParticipantMapper mapper;
    private final ParticipantService participantService;

    @Operation(summary = "Get metadata of current user")
    @GetMapping("/me")
    public ParticipantDto getCurrentUser() throws IllegalAccessException {
        return mapper.toParticipantDto(participantService.getCurrentOrThrow());
    }

    @Operation(summary = "Change password for current user")
    @PostMapping("/settings/change-password")
    public void changePassword(@Valid @RequestBody PasswordChangeDto dto) throws IllegalAccessException {
        participantService.changePassword(dto);
    }

    @Operation(summary = "Get setting for current user")
    @GetMapping("/settings")
    public SettingsDto getSettings() throws IllegalAccessException {
        return mapper.toSettingsDto(participantService.getSettings());
    }

    @Operation(summary = "Update settings for current user")
    @PatchMapping("/settings")
    public SettingsDto updateSettings(@Valid @RequestBody SettingsChangeDto dto) throws IllegalAccessException {
        return mapper.toSettingsDto(participantService.updateSettings(dto));
    }
}
