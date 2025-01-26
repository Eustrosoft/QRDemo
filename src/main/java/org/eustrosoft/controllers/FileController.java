package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.controllers.request.FileReUploadRequest;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.FileChangeDto;
import org.eustrosoft.dtos.FileDto;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/{id}")
    public FileDto findById(@PathVariable Long id) throws IllegalAccessException {
        return mapper.toDto(service.findById(id));
    }

    @PostMapping("/upload")
    public FileDto uploadFile(FileUploadRequest fur) {
        return mapper.toDto(service.uploadFile(fur));
    }

    @PostMapping("/{id}/re-upload")
    public FileDto changeFile(@PathVariable Long id, FileReUploadRequest fur) throws IllegalAccessException {
        return mapper.toDto(service.changeFile(id, fur));
    }

    @PutMapping("/{id}")
    public FileDto update(@PathVariable Long id, @RequestBody FileChangeDto dto) throws IllegalAccessException {
        dto.setId(id);
        return mapper.toDto(service.update(mapper.toEntity(dto)));
    }

    @GetMapping("/{id}/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, @PathVariable String fileName) {
        return service.downloadFile(id, fileName);
    }

    @DeleteMapping("/{id}")
    public void deleteFile(@PathVariable Long id) {
        service.delete(id);
    }
}
