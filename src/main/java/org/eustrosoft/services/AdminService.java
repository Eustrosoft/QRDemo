package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.entitites.Role;
import org.eustrosoft.repositories.projections.ParticipantAdminProjection;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ParticipantService participantService;
    private final RoleService roleService;
    private final QRRangeService qrRangeService;

    public ParticipantAdminProjection findById(Long id) {
        return participantService.getByIdAdminProjection(id);
    }

    public List<Participant> findALl() {
        return participantService.findAll();
    }

    public Collection<QR> getParticipantQrs(Long id) {
        return participantService.getById(id).getQrs();
    }

    public Participant addParticipant(Participant participant) {
        return participantService.create(participant);
    }

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
