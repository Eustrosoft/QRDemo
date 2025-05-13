package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.controllers.request.FileReUploadRequest;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.controllers.request.FileWithBlobUploadRequest;
import org.eustrosoft.dtos.FileChangeDto;
import org.eustrosoft.dtos.FileDto;
import org.eustrosoft.dtos.FileUploadResponse;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.services.FileService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController
@RequestMapping("/v1/api/secured/files")
@RequiredArgsConstructor
@Tag(name = "Files API")
public class FileController {
    private final FileService service;
    private final FileMapper mapper;

    @Operation(summary = "Find all files for current user")
    @GetMapping
    public List<FileDto> findAll() throws IllegalAccessException {
        return mapper.toListDto(service.findAllMyFiles());
    }

    @Operation(summary = "Get file by ID")
    @GetMapping("/{id}")
    public FileDto findById(@PathVariable Long id) throws IllegalAccessException {
        return mapper.toDto(service.findById(id));
    }

    @Operation(summary = "Upload file by blob protocol")
    @PostMapping("/upload/blob")
    public FileUploadResponse uploadFileBlob(FileWithBlobUploadRequest fur) {
        return service.uploadFileWithBlob(fur);
    }

    @Operation(summary = "Upload file by file request (form-data)")
    @PostMapping("/upload")
    public FileDto uploadFile(FileUploadRequest fur) {
        return mapper.toDto(service.uploadFile(fur));
    }

    @Operation(summary = "Re-upload file")
    @PostMapping("/{id}/re-upload")
    public FileDto changeFile(@PathVariable Long id, FileReUploadRequest fur) throws IllegalAccessException {
        return mapper.toDto(service.changeFile(id, fur));
    }

    @Operation(summary = "Update file metadata by ID")
    @PutMapping("/{id}")
    public FileDto update(@PathVariable Long id, @RequestBody FileChangeDto dto) throws IllegalAccessException {
        dto.setId(id);
        return mapper.toDto(service.update(mapper.toEntity(dto)));
    }

    @Operation(summary = "Download file by ID and Filename")
    @GetMapping("/{id}/download/{fileName}")
    public void downloadFile(@PathVariable Long id, @PathVariable String fileName) {
        service.downloadFile(id, fileName);
    }

    @Operation(summary = "Delete file by ID")
    @DeleteMapping("/{id}")
    public void deleteFile(@PathVariable Long id) {
        service.delete(id);
    }
}
