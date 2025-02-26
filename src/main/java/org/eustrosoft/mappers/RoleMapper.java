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

    public Role toModel(RoleDto dto) {
        Role role = super.toEntity(dto, Role.class);
        if (role == null) {
            return null;
        }
        role.setActive(dto.getActive());
        return role;
    }

    public RoleDto toDto(Role role) {
        RoleDto dto = super.toDto(role, RoleDto.class);
        if (dto == null) {
            return null;
        }
        dto.setActive(role.getActive());
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

    public List<Role> toListModels(Collection<RoleDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream().map(this::toModel)
                .collect(Collectors.toList());
    }
}
