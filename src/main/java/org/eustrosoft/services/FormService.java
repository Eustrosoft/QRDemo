package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.mappers.FormMapper;
import org.eustrosoft.repositories.FormRepository;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.repositories.projections.FormComplexProjection;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.eustrosoft.utils.CommonUtils.mergeDataAndGetString;
import static org.eustrosoft.utils.FileUtils.getFileIndex;

@Service
@RequiredArgsConstructor
public class FormService {
    private final FormMapper formMapper;
    private final FormRepository formRepository;
    private final ParticipantService participantService;
    private final SecurityComponent securityComponent;
    private final FileService fileService;

    public List<FormSimpleProjection> findAll() throws IllegalAccessException {
        return CommonUtils.iterableToList(
                formRepository.findAllByParticipantIdOrderByUpdatedDesc(participantService.getCurrentSimpleOrThrow().getId())
        );
    }

    public Optional<FormComplexProjection> get(Long id) throws IllegalAccessException {
        Optional<FormComplexProjection> form = formRepository.findById(id, FormComplexProjection.class);
        securityComponent.checkUserRightById(form.get()::getParticipantId);
        return form;
    }

    @Transactional
    public Form create(Form form) throws IllegalAccessException {
        Participant current = participantService.getCurrentSimpleOrThrow();
        form.setParticipantId(current.getId());
        if (form.getFields() != null) {
            form.getFields().forEach(ff -> {
                ff.setForm(form);
                ff.setParticipantId(current.getId());
            });
        }
        return formRepository.save(form);
    }

    @Transactional
    public Form update(Form form) throws IllegalAccessException, JsonProcessingException {
        Optional<FormComplexProjection> existedForm = get(form.getId());
        Participant current = participantService.getCurrentSimpleOrThrow();
        form.setParticipantId(current.getId());
        if (existedForm.isPresent()) {
            form.setData(mergeDataAndGetString(existedForm.get().getData(), form.getData()));
        }
        if (form.getFields() != null) {
            form.getFields().forEach(ff -> ff.setParticipantId(current.getId()));
            existedForm.ifPresent(
                    value -> form.getFields().forEach(ff -> ff.setFormId(value.getId()))
            );
        }
        return formRepository.save(form);
    }

    @Transactional
    public void delete(Long id) throws IllegalAccessException {
        get(id);
        formRepository.deleteById(id);
    }

    @Transactional
    @SneakyThrows
    public FileProjection uploadFile(Long id, FileUploadRequest fur) {
        Form form = formMapper.toEntity(get(id).get());
        FileProjection file = fileService.uploadFile(fur);
        List<File> files = form.getFiles();
        if (files == null) {
            form.setFiles(new ArrayList<>());
        }
        form.getFiles().add(new File(file.getId()));
        update(form);
        return file;
    }

    @Transactional
    @SneakyThrows
    public void deleteFile(Long id, Long fileId) {
        Form form = formMapper.toEntity(get(id).get());
        List<File> files = form.getFiles();
        if (files == null) {
            throw new IllegalArgumentException("There are no files in this form");
        }
        int index = getFileIndex(fileId, files);
        files.remove(index);
        update(form);
    }
}
