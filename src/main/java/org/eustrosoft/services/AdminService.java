package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.admin.ParticipantBlockDto;
import org.eustrosoft.dtos.admin.ParticipantChangePasswordDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.entitites.Role;
import org.eustrosoft.repositories.projections.ParticipantAdminProjection;
import org.eustrosoft.repositories.projections.ParticipantAdminSimpleProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final ParticipantService participantService;
    private final RoleService roleService;
    private final QRRangeService qrRangeService;

    @Transactional(readOnly = true)
    public ParticipantAdminProjection findById(Long id) {
        return participantService.findByIdAdminProjection(id);
    }

    @Transactional(readOnly = true)
    public List<ParticipantAdminSimpleProjection> findALl() {
        return participantService.findAll();
    }

    @Transactional(readOnly = true)
    public Collection<QR> getParticipantQrs(Long id) {
        return participantService.findById(id).getQrs();
    }

    public Participant addParticipant(Participant participant) {
        return participantService.create(participant);
    }

    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleService.findRoles();
    }

    public void deleteParticipant(Long id) throws Exception {
        participantService.delete(id);
    }

    public Participant addRangeToParticipant(Participant participant, QRRange range) {
        return participantService.addRangeToParticipant(participant, range);
    }

    public Participant revokeRangeFromParticipant(Participant participant, QRRange range) {
        return participantService.revokeRangeFromParticipant(participant, range);
    }

    @Transactional(readOnly = true)
    public List<QRRange> getFreeRanges() {
        return qrRangeService.getFreeRanges();
    }

    public void blockParticipant(ParticipantBlockDto dto) throws Exception {
        participantService.blockParticipant(dto);
    }

    public void unblockParticipant(Long id) throws Exception {
        participantService.unblockParticipant(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void changeParticipantPassword(Long id, ParticipantChangePasswordDto dto) {
        ParticipantAdminProjection participant = findById(id);
        if (!participantService.isAdmin(participant.getRoles())) {
            participantService.changePassword(id, dto.getPassword(), dto.getConfirmPassword());
        } else {
            throw new IllegalArgumentException("You can not change password for admin");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateParticipant(Long id, Participant participant) {
        ParticipantAdminProjection pap = findById(id);
        if (!participantService.isAdmin(pap.getRoles())) {
            participant.setId(id);
            participantService.update(participant);
        } else {
            throw new IllegalArgumentException("You can not change data for admin");
        }
    }
}
