package org.eustrosoft.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.EntityDto;
import org.eustrosoft.dtos.ParticipantChangeDto;
import org.eustrosoft.dtos.ParticipantDto;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.dtos.QRRangeDto;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.dtos.RoleDto;
import org.eustrosoft.dtos.admin.ParticipantBlockDto;
import org.eustrosoft.dtos.admin.ParticipantChangePasswordDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.mappers.ParticipantMapper;
import org.eustrosoft.mappers.QrMapper;
import org.eustrosoft.mappers.QrRangeMapper;
import org.eustrosoft.mappers.RoleMapper;
import org.eustrosoft.repositories.projections.ParticipantAdminSimpleProjection;
import org.eustrosoft.services.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@Validated
@RestController
@RequestMapping("/v1/api/admin/panel")
@RequiredArgsConstructor
@Api(value = "Administration Tools")
public class AdminController {
    private final AdminService adminService;
    private final QrRangeMapper qrRangeMapper;
    private final QrMapper qrMapper;
    private final ParticipantMapper participantMapper;
    private final RoleMapper roleMapper;

    @ApiOperation(value = "Get free ranges to use", response = List.class)
    @GetMapping("/ranges")
    public List<QRRangeDto> getFreeRanges() {
        return adminService.getFreeRanges()
                .stream().map(qrRangeMapper::toDto).collect(Collectors.toList());
    }

    @ApiOperation(value = "Find participant by ID", response = ParticipantDto.class)
    @GetMapping("/participants/{id}")
    public ParticipantDto findById(@PathVariable Long id) {
        return participantMapper.toDto(adminService.findById(id));
    }

    @ApiOperation(value = "Get list of participants", response = List.class)
    @GetMapping("/participants")
    public List<ParticipantAdminSimpleProjection> findAll() {
        return adminService.findALl();
    }

    @ApiOperation(value = "Find all participants QRs", response = List.class)
    @DeleteMapping("/participants/{id}/qrs")
    public List<QRDto> getParticipantQRs(@PathVariable Long id) {
        return qrMapper.toDtoList(adminService.getParticipantQrs(id));
    }

    @ApiOperation(value = "Add new participant", response = Participant.class)
    @PostMapping("/participants")
    public Participant addParticipant(@Valid @RequestBody RegistrationDto registrationDto) throws IllegalAccessException {
        return adminService.addParticipant(participantMapper.fromRegistrationDto(registrationDto));
    }

    @ApiOperation(value = "Update participant data")
    @PutMapping("/participants/{id}")
    public void updateParticipant(
            @PathVariable("id") Long id,
            @Valid @RequestBody ParticipantChangeDto dto
    ) {
        adminService.updateParticipant(id, participantMapper.fromChangeDto(dto));
    }

    @ApiOperation(value = "Change participant password")
    @PutMapping("/participants/{id}/change-password")
    public void changeParticipantPassword(
            @PathVariable("id") Long id,
            @Valid @RequestBody ParticipantChangePasswordDto dto
    ) {
        adminService.changeParticipantPassword(id, dto);
    }

    @ApiOperation(value = "Delete participant by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/participants/{id}")
    public void deleteParticipant(@PathVariable Long id) throws Exception {
        adminService.deleteParticipant(id);
    }

    @ApiOperation(value = "Add new range for participant", response = Participant.class)
    @PutMapping("/participants/{id}/ranges/add")
    public Participant addRangeToParticipant(@PathVariable Long id, @RequestBody QRRange range) {
        return adminService.addRangeToParticipant(id, range);
    }

    @ApiOperation(value = "Revoke range from participant", response = Participant.class)
    @PostMapping("/participants/ranges/revoke")
    public Participant revokeRangeFromParticipant(Participant participant, QRRange range) {
        return adminService.revokeRangeFromParticipant(participant, range);
    }

    @ApiOperation(value = "Block participant")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/participants/block")
    public void blockParticipant(@RequestBody ParticipantBlockDto dto) throws Exception {
        adminService.blockParticipant(dto);
    }

    @ApiOperation(value = "Unblock participant")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/participants/unblock")
    public void unblockParticipant(@RequestBody EntityDto dto) throws Exception {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("User id is not provided");
        }
        adminService.unblockParticipant(dto.getId());
    }

    @ApiOperation(value = "Get list of all roles", response = List.class)
    @GetMapping("/roles")
    public List<RoleDto> findAllRoles() {
        return roleMapper.toListDto(adminService.findAllRoles());
    }
}
