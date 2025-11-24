package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.controllers.request.GSLabelsRequest;
import org.eustrosoft.entitites.GSLabel;
import org.eustrosoft.services.GSLabelService;
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
@RequestMapping("/v1/api/secured/gs-labels")
@RequiredArgsConstructor
@Tag(name = "GSLabel API")
public class GSLabelController {
    private final GSLabelService service;

    @Operation(summary = "Find all GSLabel for current user")
    @GetMapping
    public List<GSLabel> findAll(GSLabelsRequest request) throws IllegalAccessException {
        return service.findAllMine(request);
    }

    @Operation(summary = "Get GSLabel by id")
    @GetMapping("/{id}")
    public GSLabel findById(@PathVariable Long id) throws IllegalAccessException {
        return service.get(id);
    }

    @Operation(summary = "Create new GSLabel")
    @PostMapping
    public GSLabel create(@Valid @RequestBody GSLabel pCode) throws Exception {
        return service.create(pCode);
    }

    @Operation(summary = "Update GSLabel")
    @PutMapping
    public GSLabel update(@Valid @RequestBody GSLabel pCode) throws IllegalAccessException, JsonProcessingException {
        return service.update(pCode);
    }

    @Operation(summary = "Delete GSLabel by id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        service.delete(id);
    }
}
