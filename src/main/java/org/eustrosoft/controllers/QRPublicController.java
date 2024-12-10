package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.mappers.QrMapper;
import org.eustrosoft.services.QRService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/unsecured/qrs")
@RequiredArgsConstructor
public class QRPublicController {
    private final QrMapper mapper;
    private final QRService service;

    @GetMapping
    public QRDto findById(@RequestParam("q") String q) throws JsonProcessingException {
        if (StringUtils.isEmpty(q)) {
            return null;
        }
        return mapper.toDto(service.getByCodePublic(Long.parseLong(q, 16)));
    }

    @GetMapping("/{id}/files/{name}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id, @PathVariable String name) throws Exception {
        return service.getFileBytesResponse(id, name);
    }
}
