package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.dtos.FormChangeDto;
import org.eustrosoft.dtos.FormCreationDto;
import org.eustrosoft.dtos.FormDto;
import org.eustrosoft.mappers.FormMapper;
import org.eustrosoft.services.FormService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/secured/forms")
public class FormsController {
    private final FormService formService;
    private final FormMapper formMapper;

    @GetMapping
    public List<FormDto> getAllForms() throws Exception {
        return formService.findAll().stream()
                .map(formMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FormDto getById(@PathVariable Long id) throws IllegalAccessException {
        return formMapper.toDto(formService.get(id).get());
    }

    @PostMapping
    public FormDto createForm(@RequestBody FormCreationDto dto) throws IllegalAccessException {
        return formMapper.toDto(
                formService.create(formMapper.fromCreationDto(dto))
        );
    }

    @PutMapping("/{id}")
    public FormDto updateForm(@RequestBody FormChangeDto dto) throws IllegalAccessException, JsonProcessingException {
        return formMapper.toDto(
                formService.update(formMapper.fromChangeDto(dto))
        );
    }

    @DeleteMapping("/{id}")
    public void deleteForm(@PathVariable Long id) throws IllegalAccessException {
        formService.delete(id);
    }
}
