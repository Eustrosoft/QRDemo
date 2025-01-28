package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.configurations.DefaultTemplateConfig;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.FileChooseRequest;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.mappers.FormMapper;
import org.eustrosoft.repositories.FormRepository;
import org.eustrosoft.repositories.projections.EntityProjection;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.repositories.projections.FormComplexProjection;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.eustrosoft.repositories.projections.FormWithFieldsProjection;
import org.eustrosoft.repositories.projections.QRSimplestProjection;
import org.eustrosoft.repositories.projections.SimpleProjection;
import org.eustrosoft.services.caches.QRCacheControlService;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.eustrosoft.services.ParticipantService.isAdmin;
import static org.eustrosoft.utils.CommonUtils.distinctByKey;
import static org.eustrosoft.utils.CommonUtils.mergeDataAndGetString;
import static org.eustrosoft.utils.FileUtils.getFileIndex;

@Service
@RequiredArgsConstructor
@Transactional
public class FormService {
    private final FormMapper formMapper;
    private final FormRepository repository;
    private final ParticipantService participantService;
    private final FileService fileService;
    private final QRCacheControlService qrCacheControlService;
    private final QRService qrService;
    private final DefaultTemplateConfig defaultTemplateConfig;

    @Transactional(readOnly = true)
    public List<FormSimpleProjection> findAll() throws IllegalAccessException {
        return CommonUtils.iterableToList(
                repository.findAllByParticipantIdOrderByUpdatedDesc(participantService.getCurrentSimpleOrThrow().getId())
        );
    }

    @Transactional(readOnly = true)
    public Optional<FormComplexProjection> get(Long id) throws IllegalAccessException {
        return repository.findByIdAndParticipantId(
                id, participantService.getCurrentOrThrow().getId(),
                FormComplexProjection.class
        );
    }

    @Transactional(readOnly = true)
    public <T extends EntityProjection> Optional<T> get(Long id, Class<T> clazz) throws IllegalAccessException {
        return repository.findByIdAndParticipantId(
                id, participantService.getCurrentOrThrow().getId(),
                clazz
        );
    }

    public Form create(Form form) throws IllegalAccessException {
        Participant current = participantService.getCurrentSimpleOrThrow();
        form.setParticipantId(current.getId());
        if (form.getFields() != null) {
            throwIfDuplicateFields(form.getFields());
            populateFieldsWithFormAndParticipantIds(
                    form.getFields(),
                    current.getId(),
                    form.getId()
            );
        }
        return repository.save(form);
    }

    public Form createDefaultForm() throws IllegalAccessException {
        Participant current = participantService.getCurrentSimpleOrThrow();
        Form defaultTemplate = defaultTemplateConfig.getDefaultTemplate();
        List<FormField> defaultFormFields = defaultTemplate.getFields();
        defaultTemplate.setFields(new ArrayList<>());
        throwIfDuplicateFields(defaultFormFields);
        Form createdForm = create(defaultTemplate);
        createdForm.setFields(defaultFormFields);
        populateFieldsWithFormAndParticipantIds(createdForm.getFields(), current.getId(), createdForm.getId());
        return repository.save(createdForm);
    }

    public Form createDefaultFormForParticipant(Long participantId) throws IllegalAccessException {
        Participant current = participantService.getCurrentOrThrow();
        if (!isAdmin(current.getRoles())) {
            throw new IllegalArgumentException("You can not create template as non admin user");
        }
        Form defaultTemplate = defaultTemplateConfig.getDefaultTemplate();
        List<FormField> defaultFormFields = defaultTemplate.getFields();
        defaultTemplate.setFields(new ArrayList<>());
        defaultTemplate.setParticipantId(participantId);
        throwIfDuplicateFields(defaultFormFields);
        Form createdForm = repository.save(defaultTemplate);
        createdForm.setFields(defaultFormFields);
        populateFieldsWithFormAndParticipantIds(createdForm.getFields(), participantId, createdForm.getId());
        return repository.save(createdForm);
    }

    public Form update(Form form) throws IllegalAccessException, JsonProcessingException {
        Optional<FormComplexProjection> existedForm = get(form.getId());
        throwIfDuplicateFields(form.getFields());
        Participant current = participantService.getCurrentSimpleOrThrow();
        form.setParticipantId(current.getId());
        if (existedForm.isPresent()) {
            form.setData(mergeDataAndGetString(existedForm.get().getData(), form.getData()));
        }
        populateFieldsWithFormAndParticipantIds(form.getFields(), current.getId(), form.getId());
        qrCacheControlService.evictFromQrsCacheByFormId(current.getId(), existedForm.get().getId());
        return repository.save(form);
    }

    public void delete(Long id) throws IllegalAccessException {
        EntityProjection form = get(id, EntityProjection.class).get();
        List<QRSimplestProjection> evicted = qrCacheControlService.evictFromQrsCacheByFormId(form.getParticipantId(), form.getId());
        repository.deleteById(id);
        if (!evicted.isEmpty()) {
            qrService.annulForm(evicted.stream().map(SimpleProjection::getId).collect(Collectors.toList()));
        }
    }

    public FileProjection uploadFile(Long id, FileUploadRequest fur) throws IllegalAccessException {
        FormComplexProjection form = get(id).get();
        FileProjection file = fileService.uploadFile(fur);
        repository.insertFile(form.getId(), file.getId());
        qrCacheControlService.evictFromQrsCacheByFormId(form.getParticipantId(), form.getId());
        return file;
    }

    public void chooseFile(Long id, FileChooseRequest fcr) throws IllegalAccessException {
        FormComplexProjection form = get(id).get();
        repository.insertFile(id, fcr.getId());
        qrCacheControlService.evictFromQrsCacheByFormId(form.getParticipantId(), form.getId());
    }

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
        qrCacheControlService.evictFromQrsCacheByFormId(form.getParticipantId(), form.getId());
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public List<FormField> findAllFields() {
        List<FormWithFieldsProjection> forms = CommonUtils.iterableToList(
                repository.findAllByParticipantId(
                        participantService.getCurrentSimpleOrThrow().getId(),
                        FormWithFieldsProjection.class
                )
        );
        return forms.stream()
                .filter(form -> form != null && form.getFields() != null)
                .flatMap(form -> form.getFields().stream())
                .filter(ff -> StringUtils.isNotBlank(ff.getName()))
                .filter(distinctByKey(FormField::getName))
                .collect(Collectors.toList());
    }

    private void populateFieldsWithFormAndParticipantIds(List<FormField> fields, Long participantId, Long formId) {
        if (fields == null || fields.isEmpty() || participantId == null || formId == null) {
            return;
        }
        fields.forEach(field -> {
            field.setParticipantId(participantId);
            field.setFormId(formId);
        });
    }

    private void throwIfDuplicateFields(List<FormField> fields) {
        if (fields == null) {
            return;
        }
        Set<String> fieldNames = new HashSet<>();
        for (FormField field : fields) {
            if (field == null || StringUtils.isEmpty(field.getName())) {
                throw new IllegalArgumentException("Field can not be null or have empty name");
            }
            boolean added = fieldNames.add(field.getName());
            if (!added) {
                throw new IllegalArgumentException("Field names can not be same");
            }
        }
    }
}
