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
@Transactional
public class FormFieldService {
    private final FormFieldRepository formFieldRepository;

    @Transactional(readOnly = true)
    public List<FormField> findAll() {
        return CommonUtils.iterableToList(formFieldRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<FormField> get(Long id) {
        return formFieldRepository.findById(id);
    }

    public FormField create(FormField formField) {
        return formFieldRepository.save(formField);
    }

    public FormField update(FormField formField) {
        return formFieldRepository.save(formField);
    }

    public void delete(Long id) {
        formFieldRepository.deleteById(id);
    }
}
