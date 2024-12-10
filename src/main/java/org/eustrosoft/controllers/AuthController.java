package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.UserLoginDto;
import org.eustrosoft.services.AuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.eustrosoft.dtos.RegistrationDto;

@RestController("Authorization")
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        return authorizationService.authorize(userLoginDto);
    }

    @PostMapping("/secured/logout")
    public ResponseEntity<?> logout() {
        return authorizationService.logout();
    }

    @PostMapping("/admin/registration")
    public ResponseEntity<?> createUser(@RequestBody RegistrationDto registrationDto) {
        return authorizationService.registerUser(registrationDto, true);
    }
}
