package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Dictionary;
import org.eustrosoft.services.DictionaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController("Dictionary")
@RequestMapping("/v1/api/secured/dictionaries")
@RequiredArgsConstructor
@Tag(name = "Dictionary API")
public class DictionaryController {
    private final DictionaryService service;

    @Operation(summary = "Get list of available dictionaries")
    @GetMapping
    public List<Dictionary> findListValues(@RequestParam String code) {
        return service.getDictionariesByCode(code);
    }

    @Operation(summary = "Get dictionary by code")
    @GetMapping("/{code}")
    public Dictionary findByCodeAndName(@PathVariable String code, @RequestParam String name) {
        return service.findByCodeAndName(code, name);
    }
}
