package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.dtos.QRChangeDto;
import org.eustrosoft.dtos.QRCreationDto;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.mappers.QrMapper;
import org.eustrosoft.services.QRService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/secured/qrs")
@RequiredArgsConstructor
public class QRController {
    private final QRService service;
    private final QrMapper mapper;

    @GetMapping
    public List<QRDto> findAll() throws IllegalAccessException {
        return service.findAllMine().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/code")
    public QRDto findById(@RequestParam("q") String q) throws IllegalAccessException {
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

    @PutMapping("/{id}/files/{name}")
    public void uploadFile(
            @PathVariable Long id,
            @PathVariable String name,
            @RequestParam("file") MultipartFile file
    ) throws IllegalAccessException, IOException {
        service.uploadFile(id, name, file);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        service.delete(id);
    }
}
