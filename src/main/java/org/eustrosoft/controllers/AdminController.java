package org.eustrosoft.controllers;

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

@Validated
@RestController
@RequestMapping("/v1/api/admin/panel")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final QrRangeMapper qrRangeMapper;
    private final QrMapper qrMapper;
    private final ParticipantMapper participantMapper;
    private final RoleMapper roleMapper;

    @GetMapping("/ranges")
    public List<QRRangeDto> getFreeRanges() {
        return adminService.getFreeRanges()
                .stream().map(qrRangeMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/participants/{id}")
    public ParticipantDto findById(@PathVariable Long id) {
        return participantMapper.toDto(adminService.findById(id));
    }

    @GetMapping("/participants")
    public List<ParticipantAdminSimpleProjection> findAll() {
        return adminService.findALl();
    }

    @DeleteMapping("/participants/{id}/qrs")
    public List<QRDto> getParticipantQRs(@PathVariable Long id) {
        return qrMapper.toDtoList(adminService.getParticipantQrs(id));
    }

    @PostMapping("/participants")
    public Participant addParticipant(@Valid @RequestBody RegistrationDto registrationDto) throws IllegalAccessException {
        return adminService.addParticipant(participantMapper.fromRegistrationDto(registrationDto));
    }

    @PutMapping("/participants/{id}")
    public void updateParticipant(
            @PathVariable("id") Long id,
            @Valid @RequestBody ParticipantChangeDto dto
    ) {
        adminService.updateParticipant(id, participantMapper.fromChangeDto(dto));
    }

    @PutMapping("/participants/{id}/change-password")
    public void changeParticipantPassword(
            @PathVariable("id") Long id,
            @Valid @RequestBody ParticipantChangePasswordDto dto
    ) {
        adminService.changeParticipantPassword(id, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/participants/{id}")
    public void deleteParticipant(@PathVariable Long id) throws Exception {
        adminService.deleteParticipant(id);
    }

    @PutMapping("/participants/{id}/ranges/add")
    public Participant addRangeToParticipant(@PathVariable Long id, @RequestBody QRRange range) {
        return adminService.addRangeToParticipant(id, range);
    }

    @PostMapping("/participants/ranges/revoke")
    public Participant revokeRangeFromParticipant(Participant participant, QRRange range) {
        return adminService.revokeRangeFromParticipant(participant, range);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/participants/block")
    public void blockParticipant(@RequestBody ParticipantBlockDto dto) throws Exception {
        adminService.blockParticipant(dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/participants/unblock")
    public void unblockParticipant(@RequestBody EntityDto dto) throws Exception {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("User id is not provided");
        }
        adminService.unblockParticipant(dto.getId());
    }

    @GetMapping("/roles")
    public List<RoleDto> findAllRoles() {
        return roleMapper.toListDto(adminService.findAllRoles());
    }
}
