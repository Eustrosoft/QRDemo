package org.eustrosoft.mappers;

import org.eustrosoft.dtos.RoleDto;
import org.eustrosoft.entitites.Role;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper extends EntityMapper {

    public RoleDto toDto(Role role) {
        RoleDto dto = super.toDto(role, RoleDto.class);
        dto.setName(role.getName());
        return dto;
    }

    public List<RoleDto> toListDto(Collection<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
