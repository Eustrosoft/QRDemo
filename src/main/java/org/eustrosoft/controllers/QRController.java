package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.controllers.request.QRRequestFilter;
import org.eustrosoft.dtos.EntityDto;
import org.eustrosoft.dtos.FileChooseRequest;
import org.eustrosoft.dtos.QRChangeDto;
import org.eustrosoft.dtos.QRCreationDto;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.mappers.QrMapper;
import org.eustrosoft.services.QRService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@Validated
@RestController
@RequestMapping("/v1/api/secured/qrs")
@RequiredArgsConstructor
@Tag(name = "QR API")
public class QRController {
    private final QRService service;
    private final QrMapper mapper;

    @Operation(summary = "Find all ranges for current user with filter")
    @GetMapping
    public List<QRDto> findAllByRange(QRRequestFilter filter) throws IllegalAccessException {
        return service.findAllMine(filter)
                .stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get QR by code")
    @GetMapping("/code")
    public QRDto findByCode(@RequestParam("q") String q) throws IllegalAccessException {
        if (StringUtils.isEmpty(q)) {
            return null;
        }
        return mapper.toDto(service.getByCode(Long.parseLong(q, 16)).get());
    }

    @Operation(summary = "Create new QR")
    @PostMapping
    public QRDto create(@Valid @RequestBody QRCreationDto dto) throws Exception {
        return mapper.toDto(service.create(mapper.fromCreationDto(dto)));
    }

    @Operation(summary = "Set form for QR object")
    @PatchMapping("/{id}")
    public QRDto setFormForQr(
            @PathVariable Long id,
            @RequestBody Long formId
    ) throws IllegalAccessException, JsonProcessingException {
        return mapper.toDto(service.setFormForQR(id, formId));
    }

    @Operation(summary = "Update data for QR")
    @PutMapping
    public QRDto update(@Valid @RequestBody QRChangeDto dto) throws IllegalAccessException, JsonProcessingException {
        return mapper.toDto(service.update(mapper.fromChangeDto(dto)));
    }

    @Operation(summary = "Upload new file in the QR")
    @PostMapping("/{id}/files/upload")
    public QRDto uploadFile(
            @PathVariable Long id,
            @Valid FileUploadRequest fur
    ) throws IllegalAccessException, IOException {
        return mapper.toDto(service.uploadFile(id, fur));
    }

    @Operation(summary = "Add file to the QR (connect)")
    @PutMapping("/{id}/files/choose")
    public void chooseFile(
            @PathVariable Long id,
            @RequestBody FileChooseRequest fcr
    ) throws IllegalAccessException, IOException {
        service.chooseFile(id, fcr);
    }

    @Operation(summary = "Delete file from QR (disconnect)")
    @PostMapping("/{id}/files/{fileId}/delete")
    public QRDto deleteFile(
            @PathVariable Long id,
            @PathVariable Long fileId
    ) throws IllegalAccessException {
        return mapper.toDto(service.deleteFile(id, fileId));
    }

    @Operation(summary = "Delete QR")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        service.delete(id);
    }
}
