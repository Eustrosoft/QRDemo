package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.eustrosoft.Constants.Version.APPLICATION_VERSION_TEXT;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController
@RequestMapping("/v1/api/unsecured/alive")
@RequiredArgsConstructor
@Tag(name = "Checking alive status and version controller")
public class AliveController {

    @Operation(summary = "Check if server is alive")
    @GetMapping
    public String alive() {
        return "Alive!";
    }

    @Operation(summary = "Get server version is string format")
    @GetMapping("/version")
    public String version() {
        return APPLICATION_VERSION_TEXT;
    }
}
