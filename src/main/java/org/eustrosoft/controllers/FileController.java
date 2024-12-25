package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.FileDto;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/secured/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService service;
    private final FileMapper mapper;

    @GetMapping
    public List<FileDto> findAll() throws IllegalAccessException {
        return mapper.toListDto(service.findAllMyFiles());
    }

    @PostMapping("/upload")
    public FileDto uploadFile(
            FileUploadRequest fur
    ) {
        return mapper.toDto(service.uploadFile(fur));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        return service.downloadFile(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFile(
            @PathVariable Long id
    ) {
        service.delete(id);
    }
}
