package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.controllers.request.FileWithBlobUploadRequest;
import org.eustrosoft.controllers.request.QRRequestFilter;
import org.eustrosoft.dtos.FileChooseRequest;
import org.eustrosoft.dtos.FileUploadResponse;
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
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@Validated
@RestController
@RequestMapping("/v1/api/secured/qrs")
@RequiredArgsConstructor
public class QRController {
    private final QRService service;
    private final QrMapper mapper;

    @ApiOperation(value = "Find all ranges for current user with filter")
    @GetMapping
    public List<QRDto> findAllByRange(QRRequestFilter filter) throws IllegalAccessException {
        return service.findAllMine(filter)
                .stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Get QR by code")
    @GetMapping("/code")
    public QRDto findByCode(@RequestParam("q") String q) throws IllegalAccessException {
        if (StringUtils.isEmpty(q)) {
            return null;
        }
        return mapper.toDto(service.getByCode(Long.parseLong(q, 16)).get());
    }

    @ApiOperation(value = "Create new QR")
    @PostMapping
    public QRDto create(@Valid @RequestBody QRCreationDto dto) throws Exception {
        return mapper.toDto(service.create(mapper.fromCreationDto(dto)));
    }

    @ApiOperation(value = "Set form for QR object")
    @PatchMapping("/{id}")
    public QRDto setFormForQr(
            @PathVariable Long id,
            @RequestBody Long formId
    ) throws IllegalAccessException, JsonProcessingException {
        return mapper.toDto(service.setFormForQR(id, formId));
    }

    @ApiOperation(value = "Update data for QR")
    @PutMapping
    public QRDto update(@Valid @RequestBody QRChangeDto dto) throws IllegalAccessException, JsonProcessingException {
        return mapper.toDto(service.update(mapper.fromChangeDto(dto)));
    }

    @ApiOperation(value = "Upload new file in the QR")
    @PostMapping("/{id}/files/upload")
    public QRDto uploadFile(
            @PathVariable Long id,
            @Valid FileUploadRequest fur
    ) throws IllegalAccessException, IOException {
        return mapper.toDto(service.uploadFile(id, fur));
    }

    @ApiOperation(value = "Add file to the QR (connect)")
    @PutMapping("/{id}/files/choose")
    public void chooseFile(
            @PathVariable Long id,
            @RequestBody FileChooseRequest fcr
    ) throws IllegalAccessException, IOException {
        service.chooseFile(id, fcr);
    }

    @ApiOperation(value = "Delete file from QR (disconnect)")
    @PostMapping("/{id}/files/{fileId}/delete")
    public QRDto deleteFile(
            @PathVariable Long id,
            @PathVariable Long fileId
    ) throws IllegalAccessException {
        return mapper.toDto(service.deleteFile(id, fileId));
    }

    @ApiOperation(value = "Delete QR")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        service.delete(id);
    }
}
