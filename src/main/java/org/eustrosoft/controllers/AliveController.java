package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.eustrosoft.Constants.Version.APPLICATION_VERSION_TEXT;

@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController
@RequestMapping("/v1/api/unsecured/alive")
@RequiredArgsConstructor
public class AliveController {

    @ApiOperation(value = "Check if server is alive")
    @GetMapping
    public String alive() {
        return "Alive!";
    }

    @ApiOperation(value = "Get server version is string format")
    @GetMapping("/version")
    public String version() {
        return APPLICATION_VERSION_TEXT;
    }
}
