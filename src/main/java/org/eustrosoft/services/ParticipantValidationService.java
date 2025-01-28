package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantValidationService {
    private final ParticipantRepository repository;

    @Transactional(readOnly = true)
    public void validateParticipantCreation(Participant participant) {
        if (Strings.isEmpty(participant.getUsername())
                || Strings.isEmpty(participant.getPassword())) {
            throw new IllegalArgumentException("Required parameters missing!");
        }
        if (repository.findByUsername(participant.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already in use!");
        }
        if (StringUtils.isNotBlank(participant.getEmail())
                && repository.findByEmail(participant.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use!");
        }
    }

    public void validateUsername(String username) {
        if (Strings.isEmpty(username)) {
            throw new IllegalArgumentException("Participant username is missing!");
        }
        if (repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already in use!");
        }
    }

    public void validateEmail(String email) {
        if (Strings.isEmpty(email)) {
            throw new IllegalArgumentException("Participant email is missing!");
        }
        if (repository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use!");
        }
    }
}
