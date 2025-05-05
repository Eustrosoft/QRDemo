package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController("Dictionary")
@RequestMapping("/v1/api/secured/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService service;

    @ApiOperation(value = "Get list of available dictionaries")
    @GetMapping
    public List<Dictionary> findListValues(@RequestParam String code) {
        return service.getDictionariesByCode(code);
    }

    @ApiOperation(value = "Get dictionary by code")
    @GetMapping("/{code}")
    public Dictionary findByCodeAndName(@PathVariable String code, @RequestParam String name) {
        return service.findByCodeAndName(code, name);
    }
}
