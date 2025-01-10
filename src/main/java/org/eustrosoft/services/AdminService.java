package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.entitites.Role;
import org.eustrosoft.repositories.projections.ParticipantAdminProjection;
import org.springframework.stereotype.Service;
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
        return participantService.getByIdAdminProjection(id);
    }

    @Transactional(readOnly = true)
    public List<Participant> findALl() {
        return participantService.findAll();
    }

    @Transactional(readOnly = true)
    public Collection<QR> getParticipantQrs(Long id) {
        return participantService.getById(id).getQrs();
    }

    public Participant addParticipant(Participant participant) {
        return participantService.create(participant);
    }

    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleService.findRoles();
    }

    public void deleteParticipant(Long id) {
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

    public void blockParticipant(Participant participant, String reason) {
        participantService.blockParticipant(participant, reason);
    }

    public void unblockParticipant(Participant participant) {
        participantService.unblockParticipant(participant);
    }
}
