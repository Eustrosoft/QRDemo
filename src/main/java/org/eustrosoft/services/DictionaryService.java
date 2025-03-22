package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.entitites.Dictionary;
import org.eustrosoft.repositories.DictionaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DictionaryService {
    private final DictionaryRepository repository;

    @SneakyThrows
    @Transactional(readOnly = true)
    public List<Dictionary> getDictionariesByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("Could not be empty code value");
        }
        return repository.findAllByCode(code);
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public Dictionary findByCodeAndName(String code, String name) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("Could not be empty code value");
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Could not be empty name value");
        }
        Optional<Dictionary> dictionary = repository.findByCodeAndName(code, name);
        if (!dictionary.isPresent()) {
            throw new EntityNotFoundException(
                    String.format(
                            "Dictionary with name = %s and code %s not found",
                            name, code
                    )
            );
        }
        return dictionary.get();
    }
}
