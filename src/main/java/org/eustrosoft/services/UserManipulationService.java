package org.eustrosoft.services;

import lombok.Setter;
import org.eustrosoft.configurations.security.UserToken;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.enums.Roles;
import org.eustrosoft.repositories.ParticipantRepository;
import org.eustrosoft.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Primary
@Service
public class UserManipulationService extends UserService {
    private final ParticipantRepository userRepository;
    private final RoleService roleService;
    @Lazy
    @Setter
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserManipulationService(
            @Autowired ParticipantRepository userRepository,
            @Autowired JwtTokenUtils jwtTokenUtils,
            @Autowired UserToken userToken,
            @Autowired RoleService roleService
    ) {
        super(userRepository, jwtTokenUtils, userToken);
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Transactional
    public Participant createUser(RegistrationDto registrationDto) {
        return createUser(registrationDto, true);
    }

    @Transactional
    public Participant createUser(RegistrationDto registrationDto, boolean active) {
        Participant participant = new Participant();
        participant.setCreated(new Date());
        participant.setUpdated(new Date());
        participant.setActive(active);
        participant.setBanned(false);
        participant.setUsername(registrationDto.getUsername());
        participant.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        participant.setEmail(registrationDto.getEmail());
        participant.setRoles(roleService.getRolesByName(Roles.USER));
        userRepository.save(participant);
        return participant;
    }

    @Transactional
    public void setActivationUser(final String username, final boolean active) {
        getByUsername(username).ifPresent(Participant -> {
            Participant.setActive(active);
            userRepository.save(Participant);
        });
    }

    @Transactional
    public void banUser(final String username, final String reason) {
        getByUsername(username).ifPresent(Participant -> {
            Participant.setBanned(true);
            Participant.setBannedReason(reason);
            userRepository.save(Participant);
        });
    }
}
