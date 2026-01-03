package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.controllers.request.PCodeRequest;
import org.eustrosoft.dtos.PCodeChangeDto;
import org.eustrosoft.dtos.PCodeCreationDto;
import org.eustrosoft.dtos.PCodeDto;
import org.eustrosoft.entitites.PCode;
import org.eustrosoft.mappers.PCodeMapper;
import org.eustrosoft.services.PCodeService;
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
import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@Validated
@RestController
@RequestMapping("/v1/api/secured/p-codes")
@RequiredArgsConstructor
@Tag(name = "PCode API")
public class PCodeController {
    private final PCodeService service;
    private final PCodeMapper mapper;

    @Operation(summary = "Find all pCodes for current user")
    @GetMapping
    public List<PCodeDto> findAll(PCodeRequest request) throws IllegalAccessException {
        return mapper.toListDto(service.findAllMine(request));
    }

    @Operation(summary = "Get pCode by docId")
    @GetMapping("/{docId}")
    public PCodeDto findById(@PathVariable Long docId) throws IllegalAccessException {
        return mapper.toDto(service.get(docId));
    }

    @Operation(summary = "Create new pCode")
    @PostMapping
    public PCodeDto create(@Valid @RequestBody PCodeCreationDto pCode) throws Exception {
        return mapper.toDto(service.create(mapper.toModel(pCode)));
    }

    @Operation(summary = "Update pCode")
    @PutMapping
    public PCodeDto update(
            @Valid @RequestBody PCodeChangeDto pCode
    ) throws IllegalAccessException, JsonProcessingException {
        return mapper.toDto(service.update(mapper.toModel(pCode)));
    }

    @Operation(summary = "Delete pCode by docId")
    @DeleteMapping("/{docId}")
    public void delete(@PathVariable Long docId) throws IllegalAccessException {
        service.delete(docId);
    }
}
