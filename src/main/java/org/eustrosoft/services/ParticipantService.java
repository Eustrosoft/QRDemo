package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.dtos.PasswordChangeDto;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.dtos.SettingsChangeDto;
import org.eustrosoft.dtos.admin.ParticipantBlockDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.entitites.Role;
import org.eustrosoft.entitites.enums.Roles;
import org.eustrosoft.mappers.ParticipantMapper;
import org.eustrosoft.repositories.ParticipantRepository;
import org.eustrosoft.repositories.projections.ParticipantAdminProjection;
import org.eustrosoft.repositories.projections.ParticipantAdminSimpleProjection;
import org.eustrosoft.repositories.projections.ParticipantSettingsProjection;
import org.eustrosoft.repositories.sub.ParticipantDataRepository;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {
    private final ParticipantRepository repository;
    private final ParticipantDataRepository pdRepository;
    private final AuthorizationService authorizationService;
    private final ParticipantValidationService participantValidationService;
    private final ParticipantMapper mapper;
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

    @Transactional(readOnly = true)
    public List<ParticipantAdminSimpleProjection> findAll() {
        return CommonUtils.iterableToList(
                repository.findAllByCreatedBeforeOrderByCreatedDesc(
                        new Date(),
                        ParticipantAdminSimpleProjection.class
                )
        );
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true, noRollbackFor = {IllegalAccessException.class})
    public Participant getCurrentOrThrow() throws IllegalAccessException {
        Optional<Participant> byToken = userService.getByToken();
        if (byToken == null || !byToken.isPresent()) {
            throw new IllegalAccessException("Can not find current user");
        }
        // TODO: make user provider or defining default security config
        try {
            return byToken.get();
        } catch (Exception ex) {
            Participant participant = new Participant();
            participant.setId(byToken.get().getId());
            participant.setUsername(byToken.get().getUsername());
            participant.setEmail(byToken.get().getEmail());
            participant.setRoles(byToken.get().getRoles());
            return participant;
        }
    }

    @Transactional(readOnly = true)
    public Participant findById(Long id) {
        return repository.findById(id, Participant.class).get();
    }

    @Transactional(readOnly = true)
    public ParticipantAdminProjection findByIdAdminProjection(Long id) {
        return repository.findById(id, ParticipantAdminProjection.class).get();
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public String getSettings() {
        return repository.findByUsername(
                userService.getUsername(),
                ParticipantSettingsProjection.class
        ).get().getSettings();
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
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
        participantValidationService.validateParticipantCreation(participant);
        participant.setPassword(passwordEncoder.encode(participant.getPassword()));
        if (CollectionUtils.isEmpty(participant.getRoles())) {
            List<Role> roles = new ArrayList<>();
            Role role = new Role();
            role.setActive(true);
            role.setName(Roles.ADMIN.getName());
            participant.setRoles(roles);
        } else {
            participant.getRoles().forEach(role -> {
                role.setId(null);
                role.setActive(true);
            });
        }
        if (!isAdmin(participant.getRoles())) {
            QRRange qrRange = qrRangeService.generateNextRange();
            List<QRRange> range = new ArrayList<>();
            range.add(qrRange);
            participant.setRanges(range);
        }
        return repository.save(participant);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Participant update(Participant participant) {
        ParticipantAdminProjection existed = findByIdAdminProjection(participant.getId());
        if (participant.getId() == null) {
            throw new IllegalArgumentException("Participant id is not provided");
        }
        if (StringUtils.isAnyBlank(participant.getUsername(), participant.getEmail())) {
            throw new IllegalArgumentException("Username or Email can not be empty");
        }
        if (!participant.getUsername().equals(existed.getUsername())) {
            participantValidationService.validateUsername(participant.getUsername());
        }
        if (!participant.getEmail().equals(existed.getEmail())) {
            participantValidationService.validateEmail(participant.getEmail());
        }
        return mapper.participantDataToParticipant(
                pdRepository.save(mapper.participantToParticipantData(participant))
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) throws IllegalAccessException {
        Participant me = getCurrentOrThrow();
        if (!isAdmin(me.getRoles())) {
            throw new IllegalAccessException("You have no admin rights");
        }
        Participant toDelete = findById(id);
        if (isAdmin(toDelete.getRoles())) {
            throw new IllegalAccessException("You can not delete admins");
        }
        repository.deleteById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void blockParticipant(ParticipantBlockDto dto) throws IllegalAccessException {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("Participant id is not provided");
        }
        Participant me = getCurrentOrThrow();
        if (!isAdmin(me.getRoles())) {
            throw new IllegalAccessException("You have no admin rights");
        }
        if (me.getId().equals(dto.getId())) {
            throw new IllegalAccessException("You can not ban yourself");
        }
        Participant toBan = findById(dto.getId());
        if (isAdmin(toBan.getRoles())) {
            throw new IllegalAccessException("You can not ban admins");
        }
        if (StringUtils.isBlank(dto.getReason())) {
            repository.setParticipantBan(dto.getId(), true);
        } else {
            repository.setParticipantBan(dto.getId(), true, dto.getReason());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unblockParticipant(Long id) throws IllegalAccessException {
        if (id == null) {
            throw new IllegalArgumentException("Participant id is not provided");
        }
        Participant me = getCurrentOrThrow();
        if (!isAdmin(me.getRoles())) {
            throw new IllegalAccessException("You have no admin rights");
        }
        Participant toUnban = findById(id);
        if (!toUnban.getBanned()) {
            throw new IllegalArgumentException("This user is not banned");
        }
        repository.setParticipantBan(id, false, "");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Participant addRangeToParticipant(Participant participant, QRRange range) {
        Optional<Participant> part = repository.findById(participant.getId());
        if (!part.isPresent()) {
            throw new IllegalArgumentException("Participant not found");
        }
        QRRange qrRange = qrRangeService.create(range);

        Collection<QRRange> ranges = part.get().getRanges();
        ranges.add(qrRange);
        update(participant);
        return findById(participant.getId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
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
        return findById(participant.getId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void changePassword(PasswordChangeDto dto) throws IllegalAccessException {
        if (StringUtils.isEmpty(dto.getOldPassword())) {
            throw new IllegalArgumentException("Old password can not be null or empty!");
        }
        if (StringUtils.isEmpty(dto.getNewPassword())
                || StringUtils.isEmpty(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New passwords can not be null or empty!");
        }
        Participant participant = getCurrentOrThrow();
        if (!passwordEncoder.matches(dto.getOldPassword(), participant.getPassword())) {
            throw new IllegalArgumentException("Not correct old password value!");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), participant.getPassword())) {
            throw new IllegalArgumentException("New and old password can not be the same!");
        }
        changePassword(participant.getId(), dto.getNewPassword(), dto.getConfirmNewPassword());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void changePassword(
            Long participantId,
            String newPassword,
            String confirmNewPassword
    ) {
        if (StringUtils.isEmpty(newPassword)
                || StringUtils.isEmpty(confirmNewPassword)) {
            throw new IllegalArgumentException("New passwords can not be null or empty!");
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("New password and confirm password are not the same!");
        }
        repository.updatePassword(participantId, passwordEncoder.encode(newPassword));
    }

    public boolean isAdmin(Collection<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        for (Role r : roles) {
            if (StringUtils.isEmpty(r.getName())) {
                throw new IllegalArgumentException("Role name is not present");
            } else {
                if (Roles.ADMIN.getName().equalsIgnoreCase(r.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
