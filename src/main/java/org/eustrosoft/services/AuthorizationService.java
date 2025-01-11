package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.dtos.UserLoginDto;
import org.eustrosoft.dtos.UserLoginResponseDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.mappers.ParticipantMapper;
import org.eustrosoft.utils.HttpUtils;
import org.eustrosoft.utils.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.eustrosoft.exceptions.Error;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.eustrosoft.configurations.security.CookieUserToken.JWT_COOKIE_NAME;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorizationService {
    private final UserManipulationService userService;
    private final JwtTokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    private final HttpUtils httpUtils;
    private final ParticipantMapper participantMapper;

    @Transactional(readOnly = true)
    public ResponseEntity<?> authorize(UserLoginDto userLoginDto) {
        try {
            ResponseEntity<?> response = validateUserLogin(userLoginDto);
            if (response != null) {
                return response;
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.UNAUTHORIZED.value(), "Not correct Participant or password."),
                    HttpStatus.UNAUTHORIZED
            );
        }

        UserDetails userDetails = userService.loadUserByUsername(userLoginDto.getUsername());
        String token = tokenUtils.generateToken(userDetails);
        httpUtils.setCookie(JWT_COOKIE_NAME, token, true, false);
        return ResponseEntity.ok(new UserLoginResponseDto(token));
    }

    public ResponseEntity<?> logout() {
        try {
            httpUtils.setCookie(JWT_COOKIE_NAME, "", true, false, 0L);
            SecurityContextHolder.getContext().setAuthentication(null);
        } catch (Exception ex) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.UNAUTHORIZED.value(), "Not correct Participant or password."),
                    HttpStatus.UNAUTHORIZED
            );
        }
        return ResponseEntity.ok("logged out successfully.");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<?> registerUser(RegistrationDto registrationDto, boolean fromAdmin) {
        ResponseEntity<?> response = validateUser(registrationDto);
        if (response != null) return response;

        final Participant createdUser = userService.createUser(registrationDto);

        return new ResponseEntity<>(
                participantMapper.toDto(createdUser),
                HttpStatus.OK
        );
    }

    @Transactional(readOnly = true)
    private ResponseEntity<?> validateUserLogin(final UserLoginDto userLoginDto) {
        Optional<Participant> participant = userService.getByUsername(userLoginDto.getUsername());
        if (!participant.isPresent()) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.BAD_REQUEST.value(), "Пользователя не существует."),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (participant.get().getBanned()) {
            String bannedReason = participant.get().getBannedReason();
            if (StringUtils.isBlank(bannedReason)) {
                bannedReason = "неизвестной причины";
            }
            return new ResponseEntity<>(
                    new Error(HttpStatus.BAD_REQUEST.value(), "Пользователь заблокирован из-за " + bannedReason),
                    HttpStatus.BAD_REQUEST
            );
        }
        return null;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> validateUser(RegistrationDto registrationDto) {
        if (Strings.isEmpty(registrationDto.getUsername())
                || Strings.isEmpty(registrationDto.getEmail())
                || Strings.isEmpty(registrationDto.getPassword())
                || Strings.isEmpty(registrationDto.getConfirmPassword())) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.BAD_REQUEST.value(), "Required fields are empty."),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.BAD_REQUEST.value(), "Passwords are not the same."),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (userService.getByUsername(registrationDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.BAD_REQUEST.value(), "Participant with this username already exists."),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (userService.getByEmail(registrationDto.getEmail()).isPresent()) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.BAD_REQUEST.value(), "Participant with this email already exists."),
                    HttpStatus.BAD_REQUEST
            );
        }
        return null;
    }
}
