package org.eustrosoft.mappers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.RegistrationRequestCreationDto;
import org.eustrosoft.dtos.RegistrationRequestDto;
import org.eustrosoft.entitites.RegistrationRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistrationRequestMapper extends EntityMapper {

    private final ParticipantMapper participantMapper;

    public RegistrationRequest toModel(RegistrationRequestCreationDto dto) {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName(dto.getFirstName());
        request.setLastName(dto.getLastName());
        request.setEmail(dto.getEmail());
        request.setCity(dto.getCity());
        request.setCountry(dto.getCountry());
        request.setPhoneNumber(dto.getPhoneNumber());
        request.setOrganization(dto.getOrganization());
        request.setWebsite(dto.getWebsite());
        return request;
    }

    public RegistrationRequestDto toDto(RegistrationRequest model) {
        if (model == null) {
            return null;
        }
        RegistrationRequestDto dto = super.toDto(model, RegistrationRequestDto.class);
        dto.setEmail(model.getEmail());
        dto.setUsername(model.getUsername());
        dto.setCity(model.getCity());
        dto.setCountry(model.getCountry());
        dto.setRegistrationId(model.getRegistrationId());
        dto.setFirstName(model.getFirstName());
        dto.setLastName(model.getLastName());
        dto.setWebSite(model.getWebsite());
        dto.setReferrerUrl(model.getReferrerUrl());
        dto.setPhoneNumber(model.getPhoneNumber());
        dto.setReviewedAt(model.getReviewedAt());
        dto.setReviewedBy(participantMapper.toDto(model.getReviewedBy()));
        dto.setOrganization(model.getOrganization());
        dto.setStatusMsg(model.getStatusMsg());
        dto.setStatus(model.getStatus());
        dto.setIpAddress(model.getIpAddress());
        dto.setUserAgent(model.getUserAgent());
        dto.setPassword(model.getPassword());
        return dto;
    }

    public List<RegistrationRequestDto> toDtoList(List<RegistrationRequest> modelList) {
        if (modelList == null) {
            return Collections.emptyList();
        }
        return modelList.stream()
                .map(this::toDto).collect(Collectors.toList());
    }
}
