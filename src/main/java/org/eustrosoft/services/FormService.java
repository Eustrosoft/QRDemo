package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.mappers.FormMapper;
import org.eustrosoft.repositories.FormRepository;
import org.eustrosoft.repositories.projections.EntityProjection;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.repositories.projections.FormComplexProjection;
import org.eustrosoft.repositories.projections.FormQrsProjection;
import org.eustrosoft.repositories.projections.FormSimpleProjection;
import org.eustrosoft.repositories.projections.FormWithFieldsProjection;
import org.eustrosoft.repositories.projections.QRSimplestProjection;
import org.eustrosoft.repositories.projections.SimpleProjection;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.eustrosoft.configurations.QRCachingConfig.QR_CACHE_NAME;
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
    private final CacheManager cacheManager;
    private final QRService qrService;

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

    public <T extends EntityProjection> Optional<T> get(Long id, Class<T> clazz) throws IllegalAccessException {
        Optional<T> form = formRepository.findById(id, clazz);
        securityComponent.checkUserRightById(form.get()::getParticipantId);
        return form;
    }

    @Transactional
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
        return formRepository.save(form);
    }

    @Transactional
    public Form update(Form form) throws IllegalAccessException, JsonProcessingException {
        Optional<FormComplexProjection> existedForm = get(form.getId());
        throwIfDuplicateFields(form.getFields());
        Participant current = participantService.getCurrentSimpleOrThrow();
        form.setParticipantId(current.getId());
        if (existedForm.isPresent()) {
            form.setData(mergeDataAndGetString(existedForm.get().getData(), form.getData()));
        }
        populateFieldsWithFormAndParticipantIds(form.getFields(), current.getId(), form.getId());
        evictFromQrsCache(current.getId(), existedForm.get().getId());
        return formRepository.save(form);
    }

    @Transactional
    public void delete(Long id) throws IllegalAccessException {
        EntityProjection form = get(id, EntityProjection.class).get();
        List<QRSimplestProjection> evicted = evictFromQrsCache(form.getParticipantId(), form.getId());
        formRepository.deleteById(id);
        if (!evicted.isEmpty()) {
            qrService.annulForm(evicted.stream().map(SimpleProjection::getId).collect(Collectors.toList()));
        }
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
        evictFromQrsCache(form.getParticipantId(), form.getId());
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
        evictFromQrsCache(form.getParticipantId(), form.getId());
    }

    @SneakyThrows
    public List<FormField> findAllFields() {
        List<FormWithFieldsProjection> forms = CommonUtils.iterableToList(
                formRepository.findAllByParticipantId(
                        participantService.getCurrentSimpleOrThrow().getId(),
                        FormWithFieldsProjection.class
                )
        );
        return forms.stream()
                .filter(form -> form != null && form.getFields() != null)
                .flatMap(form -> form.getFields().stream())
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

    private List<QRSimplestProjection> evictFromQrsCache(Long participantId, Long formId) {
        if (formId == null) {
            return Collections.emptyList();
        }
        List<QRSimplestProjection> qrs = qrService.findAllByFormIdAndParticipantId(
                participantId, formId,
                QRSimplestProjection.class
        );
        Cache qrsCache = cacheManager.getCache(QR_CACHE_NAME);
        if (qrsCache == null) {
            return Collections.emptyList();
        }
        qrs.stream().map(QRSimplestProjection::getCode)
                .filter(Objects::nonNull)
                .forEach(c -> {
                    try {
                        qrsCache.evict(c);
                    } catch (Exception e) {
                        // cache value is not present
                    }
                });
        return qrs;
    }
}
