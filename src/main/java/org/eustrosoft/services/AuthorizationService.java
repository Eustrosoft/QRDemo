package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
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

import java.util.Optional;

import static org.eustrosoft.configurations.security.CookieUserToken.JWT_COOKIE_NAME;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final UserManipulationService userService;
    private final JwtTokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    private final HttpUtils httpUtils;
    private final ParticipantMapper participantMapper;

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

    public ResponseEntity<?> registerUser(RegistrationDto registrationDto, boolean fromAdmin) {
        ResponseEntity<?> response = validateUser(registrationDto);
        if (response != null) return response;

        final Participant createdUser = userService.createUser(registrationDto);

        return new ResponseEntity<>(
                participantMapper.toDto(createdUser),
                HttpStatus.OK
        );
    }

    private ResponseEntity<?> validateUserLogin(final UserLoginDto userLoginDto) {
        Optional<Participant> Participant = userService.getByUsername(userLoginDto.getUsername());
        if (!Participant.isPresent()) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.BAD_REQUEST.value(), "Participant with this username does not exists."),
                    HttpStatus.BAD_REQUEST
            );
        }
        Participant usr = Participant.get();
        if (usr.getBanned()) {
            return new ResponseEntity<>(
                    new Error(HttpStatus.BAD_REQUEST.value(), "Participant is banned due to " + usr.getBannedReason()),
                    HttpStatus.BAD_REQUEST
            );
        }
        return null;
    }

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
