package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.FormBlock;
import org.eustrosoft.repositories.FormBlockRepository;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormBlockService {
    private final FormBlockRepository formBlockRepository;

    public List<FormBlock> findAll() {
        return CommonUtils.iterableToList(formBlockRepository.findAll());
    }

    public Optional<FormBlock> get(Long id) {
        return formBlockRepository.findById(id);
    }

    @Transactional
    public FormBlock create(FormBlock formBlock) {
        return formBlockRepository.save(formBlock);
    }

    @Transactional
    public FormBlock update(FormBlock formBlock) {
        return formBlockRepository.save(formBlock);
    }

    @Transactional
    public void delete(Long id) {
        formBlockRepository.deleteById(id);
    }
}
