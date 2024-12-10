package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.repositories.FormFieldRepository;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormFieldService {
    private final FormFieldRepository formFieldRepository;

    public List<FormField> findAll() {
        return CommonUtils.iterableToList(formFieldRepository.findAll());
    }

    public Optional<FormField> get(Long id) {
        return formFieldRepository.findById(id);
    }

    @Transactional
    public FormField create(FormField formField) {
        return formFieldRepository.save(formField);
    }

    @Transactional
    public FormField update(FormField formField) {
        return formFieldRepository.save(formField);
    }

    @Transactional
    public void delete(Long id) {
        formFieldRepository.deleteById(id);
    }
}
