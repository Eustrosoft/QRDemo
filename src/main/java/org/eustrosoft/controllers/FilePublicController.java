package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/unsecured/files")
@RequiredArgsConstructor
public class FilePublicController {
    private final FileService service;

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        return service.downloadFile(id);
    }
}
