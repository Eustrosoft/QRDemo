package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.MRange;
import org.eustrosoft.services.MRangeService;
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
@RequestMapping("/v1/api/admin/m-ranges")
@RequiredArgsConstructor
@Tag(name = "MRange API")
public class MRangeController {
    private final MRangeService service;

    @Operation(summary = "Find all MRange for current user")
    @GetMapping
    public List<MRange> findAll() throws IllegalAccessException {
        return service.findAllMine();
    }

    @Operation(summary = "Get MRange by id")
    @GetMapping("/{id}")
    public MRange findById(@PathVariable Long id) throws IllegalAccessException {
        return service.get(id);
    }

    @Operation(summary = "Create new MRange")
    @PostMapping
    public MRange create(@Valid @RequestBody MRange pCode) throws Exception {
        return service.create(pCode);
    }

    @Operation(summary = "Update MRange")
    @PutMapping
    public MRange update(@Valid @RequestBody MRange pCode) throws IllegalAccessException, JsonProcessingException {
        return service.update(pCode);
    }

    @Operation(summary = "Delete MRange by id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        service.delete(id);
    }
}
