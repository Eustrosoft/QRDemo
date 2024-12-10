package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.eustrosoft.Constants.Version.APPLICATION_VERSION_TEXT;

@RestController
@RequestMapping("/v1/api/unsecured/alive")
@RequiredArgsConstructor
public class AliveController {

    @GetMapping
    public String alive() {
        return "Alive!";
    }

    @GetMapping("/version")
    public String version() {
        return APPLICATION_VERSION_TEXT;
    }
}
