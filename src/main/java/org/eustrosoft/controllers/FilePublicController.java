package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController
@RequestMapping("/v1/api/unsecured/files")
@RequiredArgsConstructor
public class FilePublicController {
    private final FileService service;

    @ApiOperation(value = "Download file by ID and Filename")
    @GetMapping("/{id}/download/{fileName}")
    public void downloadFile(@PathVariable Long id, @PathVariable String fileName) {
        service.downloadFile(id, fileName);
    }
}
