package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.services.QRRangeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/v1/api/secured/ranges")
@RequiredArgsConstructor
public class QRRangeController {
    private final QRRangeService service;

    @GetMapping
    public Collection<QRRange> getMyRanges() throws IllegalAccessException {
        return service.getMyRanges();
    }
}
