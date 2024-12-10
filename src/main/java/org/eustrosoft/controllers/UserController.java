package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.ParticipantDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.mappers.ParticipantMapper;
import org.eustrosoft.services.RoleService;
import org.eustrosoft.services.UserManipulationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("Users")
@RequestMapping("/v1/api/")
@RequiredArgsConstructor
public class UserController {
    private final UserManipulationService userService;
    private final RoleService roleService;
    private final ParticipantMapper participantMapper;

    @GetMapping("/secured/me")
    public ParticipantDto getCurrentUser() {
        Participant user = userService.getByToken()
                .orElse(null);
        return participantMapper.toDto(user);
    }

    @GetMapping("/secured/{username}")
    public ParticipantDto getUser(@PathVariable("username") String username) {
        return participantMapper.toDto(
                userService.getByUsername(username).get()
        );
    }

    @GetMapping("/secured/roles")
    public List<String> getRoles() {
        return roleService.getRolesByToken();
    }
}
