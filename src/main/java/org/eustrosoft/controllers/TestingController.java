package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController("Test")
@RequestMapping("/v1/api")
public class TestingController {

    @ApiOperation(value = "Test if server is alive (all users)")
    @GetMapping("/alive")
    public String alive() {
        return "Alive!";
    }

    @ApiOperation(value = "Test unsecured endpoint (all users)")
    @GetMapping("/unsecured")
    public String unsecured() {
        return "Unsecured path!";
    }

    @ApiOperation(value = "Test secured endpoint (log-inned users)")
    @GetMapping("/secured")
    public String secured() {
        return "Secured path!";
    }

    @ApiOperation(value = "Test admin endpoint (only for admins)")
    @GetMapping("/admin")
    public String admin() {
        return "Admin path!";
    }
}
