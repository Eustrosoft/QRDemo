package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.ParticipantDto;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.dtos.SettingsDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.Role;
import org.eustrosoft.entitites.enums.Roles;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ParticipantMapper extends EntityMapper {
    private final RoleMapper roleMapper;
    private final QrRangeMapper qrRangeMapper;

    public ParticipantDto toDto(Participant entity) {
        ParticipantDto dto = super.toDto(entity, ParticipantDto.class);
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setRoles(
                entity.getRoles().stream()
                        .map(roleMapper::toDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public Participant fromRegistrationDto(RegistrationDto registrationDto) {
        if (registrationDto == null) {
            return null;
        }

        Participant participant = new Participant();
        participant.setUsername(registrationDto.getUsername());
        participant.setPassword(registrationDto.getPassword());
        participant.setEmail(registrationDto.getEmail());
        participant.setRoles(getRolesFromRegistrationDto(registrationDto));
        participant.setActive(true);
        participant.setBanned(false);
        return participant;
    }

    public ParticipantDto toParticipantDto(Participant participant) {
        if (participant == null) {
            return null;
        }
        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setId(participant.getId());
        participantDto.setUsername(participant.getUsername());
        participantDto.setUsername(participantDto.getUsername());
        participantDto.setEmail(participant.getEmail());
        participantDto.setCreated(participant.getCreated());
        participantDto.setUpdated(participant.getUpdated());
        participantDto.setRoles(roleMapper.toListDto(participant.getRoles()));
        if (hasNoAdminRoles(participant.getRoles())) {
            participantDto.setRanges(qrRangeMapper.toDtoList(participant.getRanges()));
        }
        return participantDto;
    }

    public List<ParticipantDto> toListDto(List<Participant> participants) {
        if (participants == null) {
            return Collections.emptyList();
        }
        return participants.stream().map(this::toParticipantDto)
                .collect(Collectors.toList());
    }

    public SettingsDto toSettingsDto(String settings) {
        if (settings == null) {
            return null;
        }
        SettingsDto dto = new SettingsDto();
        dto.setSettings(settings);
        return dto;
    }

    private List<Role> getRolesFromRegistrationDto(RegistrationDto registrationDto) {
        if (registrationDto == null || CollectionUtils.isEmpty(registrationDto.getRoles())) {
            return Collections.emptyList();
        }
        return roleMapper.toListModels(registrationDto.getRoles());
    }

    private boolean hasNoAdminRoles(List<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        for (Role role : roles) {
            if (!Roles.ADMIN.getName().equals(role.getName())) {
                return true;
            }
        }
        return false;
    }
}
