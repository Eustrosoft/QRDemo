package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.configurations.security.UserToken;
import org.eustrosoft.utils.JwtTokenUtils;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.ParticipantRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final ParticipantRepository participantRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserToken userToken;

    public Optional<Participant> getByUsername(final String username) {
        return participantRepository.findByUsername(username);
    }

    public Optional<Participant> getByEmail(final String email) {
        return participantRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Participant user = getByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public Optional<Participant> getByToken() {
        String token = userToken.getToken();
        return getByUsername(jwtTokenUtils.getUsername(token));
    }

    @Transactional
    public String getUsername() {
        return jwtTokenUtils.getUsername(userToken.getToken());
    }
}
