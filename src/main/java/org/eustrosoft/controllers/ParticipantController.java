package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@Validated
@RestController("Participant")
@RequestMapping("/v1/api/secured/participants")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantMapper mapper;
    private final ParticipantService participantService;

    @ApiOperation(value = "Get metadata of current user")
    @GetMapping("/me")
    public ParticipantDto getCurrentUser() throws IllegalAccessException {
        return mapper.toParticipantDto(participantService.getCurrentOrThrow());
    }

    @ApiOperation(value = "Change password for current user")
    @PostMapping("/settings/change-password")
    public void changePassword(@Valid @RequestBody PasswordChangeDto dto) throws IllegalAccessException {
        participantService.changePassword(dto);
    }

    @ApiOperation(value = "Get setting for current user")
    @GetMapping("/settings")
    public SettingsDto getSettings() throws IllegalAccessException {
        return mapper.toSettingsDto(participantService.getSettings());
    }

    @ApiOperation(value = "Update settings for current user")
    @PatchMapping("/settings")
    public SettingsDto updateSettings(@Valid @RequestBody SettingsChangeDto dto) throws IllegalAccessException {
        return mapper.toSettingsDto(participantService.updateSettings(dto));
    }
}
