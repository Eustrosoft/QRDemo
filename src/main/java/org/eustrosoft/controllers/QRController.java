package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.FileChooseRequest;
import org.eustrosoft.dtos.FileDto;
import org.eustrosoft.dtos.QRChangeDto;
import org.eustrosoft.dtos.QRCreationDto;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.mappers.QrMapper;
import org.eustrosoft.services.QRService;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/secured/qrs")
@RequiredArgsConstructor
public class QRController {
    private final QRService service;
    private final QrMapper mapper;
    private final FileMapper fileMapper;

    @GetMapping
    public List<QRDto> findAll() throws IllegalAccessException {
        return service.findAllMine()
                .stream().map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/code")
    public QRDto findByCode(@RequestParam("q") String q) throws IllegalAccessException {
        if (StringUtils.isEmpty(q)) {
            return null;
        }
        return mapper.toDto(service.getByCode(Long.parseLong(q, 16)).get());
    }

    @PostMapping
    public QRDto create(@RequestBody QRCreationDto dto) throws Exception {
        return mapper.toDto(service.create(mapper.fromCreationDto(dto)));
    }

    @PatchMapping("/{id}")
    public QRDto setFormForQr(
            @PathVariable Long id,
            @RequestBody Long formId
    ) throws IllegalAccessException, JsonProcessingException {
        return mapper.toDto(service.setFormForQR(id, formId));
    }

    @PutMapping
    public QRDto update(@RequestBody QRChangeDto dto) throws IllegalAccessException, JsonProcessingException {
        return mapper.toDto(service.update(mapper.fromChangeDto(dto)));
    }

    @PostMapping("/{id}/files/upload")
    public QRDto uploadFile(
            @PathVariable Long id,
            FileUploadRequest fur
    ) throws IllegalAccessException, IOException {
        return mapper.toDto(service.uploadFile(id, fur));
    }

    @PutMapping("/{id}/files/choose")
    public void chooseFile(
            @PathVariable Long id,
            @RequestBody FileChooseRequest fcr
    ) throws IllegalAccessException, IOException {
        service.chooseFile(id, fcr);
    }

    @PostMapping("/{id}/files/{fileId}/delete")
    public QRDto deleteFile(
            @PathVariable Long id,
            @PathVariable Long fileId
    ) throws IllegalAccessException {
        return mapper.toDto(service.deleteFile(id, fileId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        service.delete(id);
    }
}
