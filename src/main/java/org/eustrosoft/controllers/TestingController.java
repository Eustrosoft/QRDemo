package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController("Test")
@RequestMapping("/v1/api")
@Tag(name = "API for testing security")
public class TestingController {

    @Operation(summary = "Test if server is alive (all users)")
    @GetMapping("/alive")
    public String alive() {
        return "Alive!";
    }

    @Operation(summary = "Test unsecured endpoint (all users)")
    @GetMapping("/unsecured")
    public String unsecured() {
        return "Unsecured path!";
    }

    @Operation(summary = "Test secured endpoint (log-inned users)")
    @GetMapping("/secured")
    public String secured() {
        return "Secured path!";
    }

    @Operation(summary = "Test admin endpoint (only for admins)")
    @GetMapping("/admin")
    public String admin() {
        return "Admin path!";
    }
}
