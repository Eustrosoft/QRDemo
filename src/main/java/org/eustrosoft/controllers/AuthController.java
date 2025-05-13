package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.UserLoginDto;
import org.eustrosoft.services.AuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.eustrosoft.dtos.RegistrationDto;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController("Authorization")
@RequestMapping("/v1/api")
@RequiredArgsConstructor
@Tag(name = "Authorization Controller")
public class AuthController {
    private final AuthorizationService authorizationService;

    @Operation(summary = "Login in the system")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        return authorizationService.authorize(userLoginDto);
    }

    @Operation(summary = "Logout from system")
    @PostMapping("/secured/logout")
    public ResponseEntity<?> logout() {
        return authorizationService.logout();
    }

    @Operation(summary = "Register new user (only by admin)")
    @PostMapping("/admin/registration")
    public ResponseEntity<?> createUser(@RequestBody RegistrationDto registrationDto) {
        return authorizationService.registerUser(registrationDto, true);
    }
}
