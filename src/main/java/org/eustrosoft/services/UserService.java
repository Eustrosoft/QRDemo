package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.configurations.security.UserToken;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.ParticipantRepository;
import org.eustrosoft.utils.HttpUtils;
import org.eustrosoft.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.eustrosoft.configurations.security.CookieUserToken.JWT_COOKIE_NAME;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {
    private final ParticipantRepository participantRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserToken userToken;
    @Autowired
    private HttpUtils httpUtils;

    @Transactional(readOnly = true)
    public Optional<Participant> getByUsername(final String username) {
        return participantRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Participant> getByEmail(final String email) {
        return participantRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Optional<Participant> getByToken() {
        String token = userToken.getToken();
        Optional<Participant> participant = getByUsername(jwtTokenUtils.getUsername(token));
        if (participant.isPresent() && participant.get().getBanned()) {
            if (httpUtils != null) {
                httpUtils.setCookie(JWT_COOKIE_NAME, "", true, false, 0L);
                SecurityContextHolder.getContext().setAuthentication(null);
            }
            throw new RuntimeException("Пользователь заблокирован");
        }
        return participant;
    }

    @Transactional(readOnly = true)
    public String getUsername() {
        return jwtTokenUtils.getUsername(userToken.getToken());
    }
}
