package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.eustrosoft.dtos.MessageResponseDto;
import org.eustrosoft.dtos.RegistrationDto;
import org.eustrosoft.dtos.UserLoginDto;
import org.eustrosoft.dtos.UserLoginResponseDto;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.exceptions.CommonException;
import org.eustrosoft.exceptions.JsonApiError;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, 1001L,
                            "exceptions.title.bad_credentials",
                            "exceptions.detail.user_does_not_exist",
                            new JsonApiError.Source("credentials")
                    )
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
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.UNPROCESSABLE_ENTITY, 1002L,
                            "exceptions.title.bad_credentials",
                            "exceptions.detail.user_does_not_exist",
                            new JsonApiError.Source("credentials")
                    )
            );
        }
        return ResponseEntity.ok(new MessageResponseDto("Logged out successfully"));
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
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.bad_credentials", "exceptions.detail.user_does_not_exist",
                            new JsonApiError.Source("credentials")
                    )
            );
        }
        if (participant.get().getBanned()) {
            String bannedReason = participant.get().getBannedReason();
            if (StringUtils.isBlank(bannedReason)) {
                bannedReason = "неизвестной причины";
            }
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.user_blocked", "exceptions.detail.user_blocked",
                            new JsonApiError.Source("credentials"),
                            bannedReason
                    )
            );
        }
        return null;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> validateUser(RegistrationDto registrationDto) {
        List<JsonApiError> errors = new ArrayList<>();
        if (Strings.isEmpty(registrationDto.getUsername())) {
            errors.add(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.parameter_not_provided",
                            "exceptions.detail.username_not_provided",
                            new JsonApiError.Source("username")
                    )
            );
        }
        if (Strings.isEmpty(registrationDto.getPassword())) {
            errors.add(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.parameter_not_provided",
                            "exceptions.detail.password_not_provided",
                            new JsonApiError.Source("password")
                    )
            );
        }
        if (Strings.isEmpty(registrationDto.getConfirmPassword())) {
            errors.add(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.parameter_not_provided",
                            "exceptions.detail.confirm_password_not_provided",
                            new JsonApiError.Source("confirm_password")
                    )
            );
        }
        if (!errors.isEmpty()) {
            throw new CommonException(errors);
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            errors.add(
                    new JsonApiError(
                        HttpStatus.BAD_REQUEST, -1L,
                        "exceptions.title.passwords_are_not_same",
                        "exceptions.detail.passwords_are_not_same",
                        new JsonApiError.Source("password")
                    )
            );
            errors.add(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.passwords_are_not_same",
                            "exceptions.detail.passwords_are_not_same",
                            new JsonApiError.Source("confirm_password")
                    )
            );
        }
        if (!errors.isEmpty()) {
            throw new CommonException(errors);
        }
        if (userService.getByUsername(registrationDto.getUsername()).isPresent()) {
            errors.add(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.user_already_exists",
                            "exceptions.detail.user_with_username_exists",
                            new JsonApiError.Source("username")
                    )
            );
        }
        if (!errors.isEmpty()) {
            throw new CommonException(errors);
        }
        if (StringUtils.isNotBlank(registrationDto.getEmail())
                && userService.getByEmail(registrationDto.getEmail()).isPresent()) {
            errors.add(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, -1L,
                            "exceptions.title.user_already_exists",
                            "exceptions.detail.user_with_email_exists",
                            new JsonApiError.Source("email")
                    )
            );
        }
        return null;
    }
}
