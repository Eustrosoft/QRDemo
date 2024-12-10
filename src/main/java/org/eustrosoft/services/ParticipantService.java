package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.eustrosoft.dtos.PasswordChangeDto;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.dtos.SettingsChangeDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.entitites.Role;
import org.eustrosoft.repositories.projections.ParticipantAdminProjection;
import org.eustrosoft.repositories.ParticipantRepository;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository repository;
    private final RoleService roleService;
    private final AuthorizationService authorizationService;
    @Lazy
    @Setter
    @Autowired
    private QRRangeService qrRangeService;
    @Lazy
    @Setter
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Lazy
    @Setter
    @Autowired
    private UserService userService;

    public Participant getCurrentSimpleOrThrow() throws IllegalAccessException {
        Optional<Participant> byToken = userService.getByToken();
        if (!byToken.isPresent()) {
            throw new IllegalAccessException("Can not find current user");
        }
        Participant participant = new Participant();
        participant.setId(byToken.get().getId());
        participant.setUsername(byToken.get().getUsername());
        participant.setEmail(byToken.get().getEmail());
        return participant;
    }

    public Participant getCurrentOrThrow() throws IllegalAccessException {
        Optional<Participant> byToken = userService.getByToken();
        if (!byToken.isPresent()) {
            throw new IllegalAccessException("Can not find current user");
        }
        // TODO: make user provider or defining default security config
        try {
            return getById(byToken.get().getId());
        } catch (Exception ex) {
            Participant participant = new Participant();
            participant.setId(byToken.get().getId());
            participant.setUsername(byToken.get().getUsername());
            participant.setEmail(byToken.get().getEmail());
            participant.setRoles(byToken.get().getRoles());
            return participant;
        }
    }

    public Participant getById(Long id) {
        return repository.findById(id).get();
    }

    public ParticipantAdminProjection getByIdAdminProjection(Long id) {
        return repository.findById(id, ParticipantAdminProjection.class).get();
    }

    @SneakyThrows
    public String getSettings() {
        Optional<Participant> byToken = userService.getByToken();
        if (!byToken.isPresent()) {
            throw new IllegalAccessException("Can not find current user");
        }
        return byToken.get().getSettings();
    }

    @SneakyThrows
    @Transactional
    public String updateSettings(SettingsChangeDto settings) {
        if (settings == null || settings.getSettings() == null || settings.getSettings().isEmpty()) {
            throw new IllegalArgumentException("Illegal settings parameter");
        }
        Optional<Participant> byToken = userService.getByToken();
        if (!byToken.isPresent()) {
            throw new IllegalAccessException("Can not find current user");
        }
        repository.updateSettings(byToken.get().getId(), settings.getSettings().toString());
        return getSettings();
    }

    public List<Participant> findAll() {
        return CommonUtils.iterableToList(repository.findAll());
    }

    @Transactional
    public Participant create(Participant participant) {
        ResponseEntity<?> resp = authorizationService.validateUser(
                new RegistrationDto(
                        participant.getUsername(), participant.getPassword(),
                        participant.getPassword(), participant.getEmail()
                )
        );
        if (resp != null) {
            throw new IllegalArgumentException("Invalid user credentials");
        }
        validateParticipant(participant);
        participant.setPassword(passwordEncoder.encode(participant.getPassword()));
        if (CollectionUtils.isEmpty(participant.getRoles())) {
            List<Role> roles = roleService.getRolesByName(Role.Names.USER);
            participant.setRoles(roles);
        }
        if (!containsRoleAdmin(participant.getRoles())) {
            QRRange qrRange = qrRangeService.generateNextRange();
            List<QRRange> range = new ArrayList<>();
            range.add(qrRange);
            participant.setRanges(range);
        }
        return repository.save(participant);
    }

    private void validateParticipant(Participant participant) {
        if (Strings.isEmpty(participant.getUsername())
                || Strings.isEmpty(participant.getEmail())
                || Strings.isEmpty(participant.getPassword())) {
            throw new IllegalArgumentException("Required parameters missing!");
        }
        if (repository.findByUsername(participant.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already in use!");
        }
        if (repository.findByEmail(participant.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use!");
        }
    }

    @Transactional
    public Participant update(Participant participant) {
        return repository.save(participant);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void blockParticipant(Participant participant, String reason) {
        participant.setBanned(true);
        if (StringUtils.isNotBlank(reason)) {
            participant.setBannedReason(reason);
        }
        repository.save(participant);
    }

    @Transactional
    public void unblockParticipant(Participant participant) {
        participant.setBanned(false);
        repository.save(participant);
    }

    @Transactional
    public Participant addRangeToParticipant(Participant participant, QRRange range) {
        Optional<Participant> part = repository.findById(participant.getId());
        if (!part.isPresent()) {
            throw new IllegalArgumentException("Participant not found");
        }
        QRRange qrRange = qrRangeService.create(range);

        Collection<QRRange> ranges = part.get().getRanges();
        ranges.add(qrRange);
        update(participant);
        return getById(participant.getId());
    }

    @Transactional
    public Participant revokeRangeFromParticipant(Participant participant, QRRange range) {
        Optional<Participant> part = repository.findById(participant.getId());
        if (!part.isPresent()) {
            throw new IllegalArgumentException("Participant not found");
        }
        Collection<QRRange> ranges = part.get().getRanges();
        Optional<QRRange> first = ranges.stream().filter(r -> r.getId().equals(range.getId())).findFirst();
        first.ifPresent(r -> {
            ranges.remove(r);
            qrRangeService.delete(r.getId());
        });
        update(participant);
        return getById(participant.getId());
    }

    private boolean containsRoleAdmin(Collection<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        Role adminRole = roleService
                .getRolesByName(Role.Names.ADMIN).get(0);
        for (Role r : roles) {
            if (StringUtils.isEmpty(r.getName())) {
                Long roleId = r.getId();
                if (adminRole.getId().equals(roleId)) {
                    return true;
                }
            } else {
                if (Role.Names.ADMIN.getName().equalsIgnoreCase(r.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void changePassword(PasswordChangeDto dto) throws IllegalAccessException {
        if (StringUtils.isEmpty(dto.getOldPassword())) {
            throw new IllegalArgumentException("Old password can not be null or empty!");
        }
        if (StringUtils.isEmpty(dto.getNewPassword())
                || StringUtils.isEmpty(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New passwords can not be null or empty!");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirm password are not the same!");
        }
        Participant participant = getCurrentOrThrow();
        if (!passwordEncoder.matches(dto.getOldPassword(), participant.getPassword())) {
            throw new IllegalArgumentException("Not correct old password value!");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), participant.getPassword())) {
            throw new IllegalArgumentException("New and old password can not be the same!");
        }
        participant.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(participant);
    }
}
