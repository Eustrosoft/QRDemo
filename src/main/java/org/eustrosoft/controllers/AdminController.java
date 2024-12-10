package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.ParticipantDto;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.dtos.QRRangeDto;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.dtos.RoleDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.mappers.ParticipantMapper;
import org.eustrosoft.mappers.QrMapper;
import org.eustrosoft.mappers.QrRangeMapper;
import org.eustrosoft.mappers.RoleMapper;
import org.eustrosoft.repositories.projections.ParticipantAdminProjection;
import org.eustrosoft.services.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ParticipantAdminProjection findById(@PathVariable Long id) {
        return adminService.findById(id);
    }

    @GetMapping("/participants")
    public List<ParticipantDto> findAll() {
        return participantMapper.toListDto(adminService.findALl());
    }

    @DeleteMapping("/participants/{id}/qrs")
    public List<QRDto> getParticipantQRs(@PathVariable Long id) {
        return qrMapper.toDtoList(adminService.getParticipantQrs(id));
    }

    @PostMapping("/participants")
    public Participant addParticipant(@RequestBody RegistrationDto registrationDto) {
        return adminService.addParticipant(participantMapper.fromRegistrationDto(registrationDto));
    }

    @DeleteMapping("/participants/{id}")
    public void deleteParticipant(@PathVariable Long id) {
        adminService.deleteParticipant(id);
    }

    @PutMapping("/participants/ranges/add")
    public Participant addRangeToParticipant(Participant participant, QRRange range) {
        return adminService.addRangeToParticipant(participant, range);
    }

    @PostMapping("/participants/ranges/revoke")
    public Participant revokeRangeFromParticipant(Participant participant, QRRange range) {
        return adminService.revokeRangeFromParticipant(participant, range);
    }

    @PostMapping("/participants/block")
    public void blockParticipant(Participant participant, String reason) {
        adminService.blockParticipant(participant, reason);
    }

    @PostMapping("/participants/unblock")
    public void unblockParticipant(Participant participant) {
        adminService.unblockParticipant(participant);
    }

    @GetMapping("/roles")
    public List<RoleDto> findAllRoles() {
        return roleMapper.toListDto(adminService.findAllRoles());
    }
}
