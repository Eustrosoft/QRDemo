package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.dtos.ParticipantChangeDto;
import org.eustrosoft.dtos.ParticipantDto;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.dtos.SettingsDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.Role;
import org.eustrosoft.entitites.enums.Roles;
import org.eustrosoft.entitites.subentities.ParticipantData;
import org.eustrosoft.repositories.projections.ParticipantAdminProjection;
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
    private final QrMapper qrMapper;

    public ParticipantDto toDto(Participant entity) {
        ParticipantDto dto = super.toDto(entity, ParticipantDto.class);
        if (dto == null) {
            return null;
        }
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setLei(entity.getLei());
        dto.setOrganization(entity.getOrganization());
        dto.setAddress(entity.getAddress());
        dto.setWebsite(entity.getWebsite());
        dto.setRoles(
                entity.getRoles().stream()
                        .map(roleMapper::toDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public ParticipantDto toDto(ParticipantAdminProjection entity) {
        ParticipantDto dto = super.toDtoFromProjection(entity, ParticipantDto.class);
        if (dto == null) {
            return null;
        }
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setLei(entity.getLei());
        dto.setOrganization(entity.getOrganization());
        dto.setAddress(entity.getAddress());
        dto.setWebsite(entity.getWebsite());
        dto.setBanned(entity.getBanned());
        dto.setBannedReason(entity.getBannedReason());
        dto.setActive(entity.getActive());
        dto.setRoles(roleMapper.toListDto(entity.getRoles()));
        dto.setQrs(qrMapper.toDtoListProjections(entity.getQrs()));
        dto.setRanges(qrRangeMapper.toDtoList(entity.getRanges()));
        return dto;
    }

    public Participant fromRegistrationDto(RegistrationDto registrationDto) {
        if (registrationDto == null) {
            return null;
        }

        Participant participant = new Participant();
        participant.setUsername(registrationDto.getUsername());
        participant.setPassword(registrationDto.getPassword());
        participant.setEmail(StringUtils.getIfBlank(registrationDto.getEmail(), () -> null));
        participant.setLei(registrationDto.getLei());
        participant.setOrganization(registrationDto.getOrganization());
        participant.setAddress(registrationDto.getAddress());
        participant.setWebsite(registrationDto.getWebsite());
        participant.setRoles(getRolesFromRegistrationDto(registrationDto));
        participant.setActive(true);
        participant.setBanned(false);
        return participant;
    }

    public Participant fromChangeDto(ParticipantChangeDto dto) {
        if (dto == null) {
            return null;
        }

        Participant participant = new Participant();
        participant.setUsername(dto.getUsername());
        participant.setDescription(dto.getDescription());
        participant.setEmail(dto.getEmail());
        participant.setLei(dto.getLei());
        participant.setOrganization(dto.getOrganization());
        participant.setAddress(dto.getAddress());
        participant.setWebsite(dto.getWebsite());
        participant.setRoles(getRolesFromParticipantDto(dto));
        return participant;
    }

    public ParticipantDto toParticipantDto(Participant participant) {
        if (participant == null) {
            return null;
        }
        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setId(participant.getId());
        participantDto.setUsername(participant.getUsername());
        participantDto.setEmail(participant.getEmail());
        participantDto.setCreated(participant.getCreated());
        participantDto.setUpdated(participant.getUpdated());
        participantDto.setLei(participant.getLei());
        participantDto.setOrganization(participant.getOrganization());
        participantDto.setAddress(participant.getAddress());
        participantDto.setWebsite(participant.getWebsite());
        participantDto.setRoles(roleMapper.toListDto(participant.getRoles()));
        participantDto.setRanges(qrRangeMapper.toDtoList(participant.getRanges()));
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

    public ParticipantData participantToParticipantData(Participant participant) {
        ParticipantData pd = new ParticipantData();
        pd.setId(participant.getId());
        pd.setOrganization(participant.getOrganization());
        pd.setDescription(participant.getDescription());
        pd.setLei(participant.getLei());
        pd.setAddress(participant.getAddress());
        pd.setWebsite(participant.getWebsite());
        pd.setUsername(participant.getUsername());
        pd.setEmail(participant.getEmail());
        return pd;
    }

    public Participant participantDataToParticipant(ParticipantData participantData) {
        Participant participant = new Participant();
        participant.setId(participantData.getId());
        participant.setOrganization(participantData.getOrganization());
        participant.setDescription(participantData.getDescription());
        participant.setLei(participantData.getLei());
        participant.setAddress(participantData.getAddress());
        participant.setWebsite(participantData.getWebsite());
        participant.setUsername(participantData.getUsername());
        participant.setEmail(participantData.getEmail());
        participant.setCreated(participantData.getCreated());
        participant.setUpdated(participantData.getUpdated());
        participant.setParticipantId(participantData.getParticipantId());
        return participant;
    }

    private List<Role> getRolesFromRegistrationDto(RegistrationDto registrationDto) {
        if (registrationDto == null || CollectionUtils.isEmpty(registrationDto.getRoles())) {
            return Collections.emptyList();
        }
        return roleMapper.toListModels(registrationDto.getRoles());
    }

    private List<Role> getRolesFromParticipantDto(ParticipantDto dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getRoles())) {
            return Collections.emptyList();
        }
        return roleMapper.toListModels(dto.getRoles());
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
