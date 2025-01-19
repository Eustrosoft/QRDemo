package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.FormChangeDto;
import org.eustrosoft.dtos.FormCreationDto;
import org.eustrosoft.dtos.FormDto;
import org.eustrosoft.dtos.FormFieldDto;
import org.eustrosoft.mappers.FormFieldMapper;
import org.eustrosoft.mappers.FormMapper;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.services.FormService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/secured/forms")
public class FormsController {
    private final FormService formService;
    private final FormMapper formMapper;
    private final FormFieldMapper formFieldMapper;

    @GetMapping
    public List<FormDto> findAll() throws Exception {
        return formService.findAll().stream()
                .map(formMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FormDto findById(@PathVariable Long id) throws IllegalAccessException {
        return formMapper.toDto(formService.get(id).get());
    }

    @PostMapping
    public FormDto createForm(@RequestBody FormCreationDto dto) throws IllegalAccessException {
        return formMapper.toDto(
                formService.create(formMapper.fromCreationDto(dto))
        );
    }

    @PostMapping("/{id}/files/upload")
    public FileProjection uploadFile(
            @PathVariable Long id,
            FileUploadRequest fur
    ) throws IllegalAccessException {
        return formService.uploadFile(id, fur);
    }

    @PostMapping("/{id}/files/{fileId}/delete")
    public void deleteFile(
            @PathVariable Long id,
            @PathVariable Long fileId
    ) throws IllegalAccessException {
        formService.deleteFile(id, fileId);
    }

    @PutMapping("/{id}")
    public FormDto update(@RequestBody FormChangeDto dto) throws IllegalAccessException, JsonProcessingException {
        return formMapper.toDto(
                formService.update(formMapper.fromChangeDto(dto))
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        formService.delete(id);
    }

    @GetMapping("/fields")
    public List<FormFieldDto> findAllFormFields() {
        return formFieldMapper.toListDto(formService.findAllFields());
    }
}
