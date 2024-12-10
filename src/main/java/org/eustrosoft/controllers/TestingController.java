package org.eustrosoft.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController("Test")
@RequestMapping("/v1/api")
public class TestingController {

    @GetMapping("/")
    public String alive() {
        return "Alive!";
    }

    @GetMapping("/unsecured")
    public String unsecured() {
        return "Unsecured path!";
    }

    @GetMapping("/secured")
    public String secured() {
        return "Secured path!";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin path!";
    }

    @GetMapping("/me")
    public String getUserName(Principal principal) {
        return principal.getName();
    }
}
