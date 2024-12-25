package org.eustrosoft.controllers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Dictionary;
import org.eustrosoft.services.DictionaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("Dictionary")
@RequestMapping("/v1/api/secured/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService service;

    @GetMapping
    public List<Dictionary> findListValues(@RequestParam String code) {
        return service.getDictionariesByCode(code);
    }
}
