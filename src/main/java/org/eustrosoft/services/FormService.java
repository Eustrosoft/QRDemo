package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.FormRepository;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.eustrosoft.utils.CommonUtils.mergeDataAndGetString;

@Service
@RequiredArgsConstructor
public class FormService {
    private final FormRepository formRepository;
    private final ParticipantService participantService;
    private final SecurityComponent securityComponent;

    public List<FormSimpleProjection> findAll() throws IllegalAccessException {
        return CommonUtils.iterableToList(
                formRepository.findAllByParticipantOrderByUpdated(participantService.getCurrentSimpleOrThrow())
        );
    }

    public Optional<Form> get(Long id) throws IllegalAccessException {
        Optional<Form> form = formRepository.findById(id);
        securityComponent.checkUserRight(form.get()::getParticipant);
        return form;
    }

    @Transactional
    public Form create(Form form) throws IllegalAccessException {
        Participant current = participantService.getCurrentSimpleOrThrow();
        form.setParticipant(current);
        return formRepository.save(form);
    }

    @Transactional
    public Form update(Form form) throws IllegalAccessException, JsonProcessingException {
        Optional<Form> existedForm = get(form.getId());
        Participant current = participantService.getCurrentSimpleOrThrow();
        form.setParticipant(current);
        if (existedForm.isPresent()) {
            form.setData(mergeDataAndGetString(existedForm.get().getData(), form.getData()));
        }
        return formRepository.save(form);
    }

    @Transactional
    public void delete(Long id) throws IllegalAccessException {
        get(id);
        formRepository.deleteById(id);
    }
}
