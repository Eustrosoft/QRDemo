package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.services.QRService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/api/unsecured/qrs")
@RequiredArgsConstructor
public class QRPublicController {
    private final QRService service;

    @GetMapping
    public QRDto findById(@RequestParam("q") String q) throws JsonProcessingException {
        if (StringUtils.isEmpty(q)) {
            return null;
        }
        return service.getByCodePublic(Long.parseLong(q, 16));
    }

    @GetMapping("/files/all/download")
    public void downloadAllPublicFilesZip(@RequestParam("q") String q) throws IOException {
        service.downloadAllPublicFiles(Long.parseLong(q, 16));
    }
}
