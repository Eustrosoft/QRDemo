package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.entitites.Dictionary;
import org.eustrosoft.repositories.DictionaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictionaryService {
    private final DictionaryRepository repository;

    @SneakyThrows
    public List<Dictionary> getDictionariesByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("Could not be empty code value");
        }
        return repository.findAllByCode(code);
    }
}
