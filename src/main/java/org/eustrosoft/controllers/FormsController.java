package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.FileChooseRequest;
import org.eustrosoft.dtos.FormChangeDto;
import org.eustrosoft.dtos.FormCreationDto;
import org.eustrosoft.dtos.FormDto;
import org.eustrosoft.dtos.FormFieldDto;
import org.eustrosoft.mappers.FormFieldMapper;
import org.eustrosoft.mappers.FormMapper;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.services.FormService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequiredArgsConstructor
@RequestMapping("/v1/api/secured/forms")
@Tag(name = "Forms API")
public class FormsController {
    private final FormService service;
    private final FormMapper formMapper;
    private final FormFieldMapper formFieldMapper;

    @Operation(summary = "Find all forms for current user")
    @GetMapping
    public List<FormDto> findAll() throws Exception {
        return service.findAll().stream()
                .map(formMapper::toDto).collect(Collectors.toList());
    }

    @Operation(summary = "Find form by ID")
    @GetMapping("/{id}")
    public FormDto findById(@PathVariable Long id) throws IllegalAccessException {
        return formMapper.toDto(service.get(id).get());
    }

    @Operation(summary = "Create new form")
    @PostMapping
    public FormDto createForm(@Valid @RequestBody FormCreationDto dto) throws IllegalAccessException {
        return formMapper.toDto(
                service.create(formMapper.fromCreationDto(dto))
        );
    }

    @Operation(summary = "Create default form with filled fields")
    @PostMapping("/default")
    public FormDto createDefaultForm() throws IllegalAccessException, JsonProcessingException {
        return formMapper.toDto(service.createDefaultForm());
    }

    @Operation(summary = "Upload file in the form")
    @PostMapping("/{id}/files/upload")
    public FileProjection uploadFile(
            @PathVariable Long id,
            @Valid FileUploadRequest fur
    ) throws IllegalAccessException {
        return service.uploadFile(id, fur);
    }

    @Operation(summary = "Choose file from existing and connect it to the form")
    @PutMapping("/{id}/files/choose")
    public void chooseFile(
            @PathVariable Long id,
            @RequestBody FileChooseRequest fcr
    ) throws IllegalAccessException, IOException {
        service.chooseFile(id, fcr);
    }

    @Operation(summary = "Delete file from form (disconnect)")
    @PostMapping("/{id}/files/{fileId}/delete")
    public void deleteFile(
            @PathVariable Long id,
            @PathVariable Long fileId
    ) throws IllegalAccessException {
        service.deleteFile(id, fileId);
    }

    @Operation(summary = "Update form metadata by ID")
    @PutMapping("/{id}")
    public FormDto update(
            @Valid @RequestBody FormChangeDto dto
    ) throws IllegalAccessException, JsonProcessingException {
        return formMapper.toDto(
                service.update(formMapper.fromChangeDto(dto))
        );
    }

    @Operation(summary = "Delete form by ID")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        service.delete(id);
    }

    @Operation(summary = "Get all available unique fields")
    @GetMapping("/fields")
    public List<FormFieldDto> findAllFormFields() {
        return formFieldMapper.toListDto(service.findAllFields());
    }
}
