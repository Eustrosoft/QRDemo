package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController("Authorization")
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthorizationService authorizationService;

    @ApiOperation(value = "Login in the system")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        return authorizationService.authorize(userLoginDto);
    }

    @ApiOperation(value = "Logout from system")
    @PostMapping("/secured/logout")
    public ResponseEntity<?> logout() {
        return authorizationService.logout();
    }

    @ApiOperation(value = "Register new user (only by admin)")
    @PostMapping("/admin/registration")
    public ResponseEntity<?> createUser(@RequestBody RegistrationDto registrationDto) {
        return authorizationService.registerUser(registrationDto, true);
    }
}
