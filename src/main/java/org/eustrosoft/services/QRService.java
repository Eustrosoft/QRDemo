package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.eustrosoft.configurations.QRRangeConfig;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.FileChooseRequest;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.mappers.FormMapper;
import org.eustrosoft.mappers.QrMapper;
import org.eustrosoft.repositories.QRRepository;
import org.eustrosoft.repositories.projections.EntityProjection;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.repositories.projections.FormComplexProjection;
import org.eustrosoft.repositories.projections.QRProjection;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.eustrosoft.repositories.projections.QRSimplestProjection;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.eustrosoft.utils.CompressUtils;
import org.eustrosoft.utils.JdbcBlobProcessor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.eustrosoft.Constants.EMPTY_JSON;
import static org.eustrosoft.configurations.QRCachingConfig.QR_CACHE_NAME;
import static org.eustrosoft.utils.CommonUtils.mergeDataAndGetString;
import static org.eustrosoft.utils.CompressUtils.zipFile;

@Service
@RequiredArgsConstructor
@Transactional
public class QRService {
    private final QRRangeConfig qrRangeConfig;
    private final QRRepository qrRepository;
    private final ParticipantService participantService;
    private final SecurityComponent securityComponent;
    private final QrMapper qrMapper;
    private final FormMapper formMapper;
    private final FileMapper fileMapper;
    private final FileService fileService;
    private final JdbcBlobProcessor jdbcBlobProcessor;

    @Transactional(readOnly = true)
    public Optional<QR> get(Long id) throws IllegalAccessException {
        Optional<QR> byId = qrRepository.findById(id);
        securityComponent.checkUserRightById(byId.get()::getParticipantId);
        return byId;
    }

    @Transactional(readOnly = true)
    public <T extends EntityProjection> Optional<T> get(Long id, Class<T> clazz) throws IllegalAccessException {
        Optional<T> byId = qrRepository.findById(id, clazz);
        securityComponent.checkUserRightById(byId.get()::getParticipantId);
        return byId;
    }

    @Transactional(readOnly = true)
    public Optional<QRProjection> getByCode(Long code) throws IllegalAccessException {
        Optional<QRProjection> byId = qrRepository.findByCode(code, QRProjection.class);
        securityComponent.checkUserRightById(byId.get()::getParticipantId);
        return byId;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = QR_CACHE_NAME)
    public QRDto getByCodePublic(Long code) throws JsonProcessingException {
        if (code == null) {
            throw new IllegalArgumentException("Illegal code");
        }
        Optional<QRProjection> qr = qrRepository.findByCode(code, QRProjection.class);
        if (!qr.isPresent()) {
            throw new IllegalArgumentException("QR code not found");
        }
        QRProjection gotQr = qr.get();
        return prepareDataBasedOnForm(gotQr);
    }

    @Transactional(readOnly = true)
    public void downloadAllPublicFiles(Long code) throws IOException {
        if (code == null) {
            throw new IllegalArgumentException("Illegal code");
        }
        Optional<QRProjection> qr = qrRepository.findByCode(code, QRProjection.class);
        if (!qr.isPresent()) {
            throw new IllegalArgumentException("QR code not found");
        }
        QRProjection gotQr = qr.get();
        List<FileProjection> publicFiles = new ArrayList<>();
        List<FileProjection> qrFiles = gotQr.getFiles();
        if (qrFiles != null && !qrFiles.isEmpty()) {
            qrFiles.stream()
                    .filter(f -> f.getIsPublic() && f.getIsActive())
                    .forEach(publicFiles::add);
        }
        FormComplexProjection form = gotQr.getForm();
        if (form != null && form.getFiles() != null && !form.getFiles().isEmpty()) {
            form.getFiles().stream()
                    .filter(f -> f.getIsPublic() && f.getIsActive())
                    .forEach(publicFiles::add);
        }
        downloadFiles(code, publicFiles);
    }

    @Transactional(readOnly = true)
    public List<QRSimpleProjection> findAllMine() throws IllegalAccessException {
        return CommonUtils.iterableToList(
                qrRepository.findAllByParticipantIdOrderByCodeDesc(
                        participantService.getCurrentSimpleOrThrow().getId()
                )
        );
    }

    @Transactional(readOnly = true)
    public <T extends EntityProjection> List<T> findAllByFormId(
            Long formId,
            Class<T> clazz
    ) throws IllegalAccessException {
        return CommonUtils.iterableToList(
                qrRepository.findAllByParticipantIdAndFormId(
                        participantService.getCurrentSimpleOrThrow().getId(),
                        formId,
                        clazz
                )
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QR create(QR qr) throws IllegalAccessException, IllegalArgumentException {
        Participant current = participantService.getCurrentOrThrow();
        if (qr.getCode() == null) {
            qr.setCode(getNextAvailableQR(current.getRanges(), current.getQrs()));
        } else {
            checkUsedQr(current.getRanges(), current.getQrs(), qr.getCode());
        }
        // TODO: removed due to availability to set qr range out of range
        // Long code = qr.getCode();
//        if (code < qrRangeConfig.getRangeStart() || code > qrRangeConfig.getRangeEnd()) {
//            throw new IllegalArgumentException("Code has illegal character");
//        }
        qr.setParticipantId(current.getId());
        return qrRepository.save(qr);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(key = "#result.code",value = QR_CACHE_NAME)
    public QR setFormForQR(Long id, Long formId) throws IllegalAccessException, JsonProcessingException {
        Optional<QR> qr = get(id);
        if (qr.isPresent()) {
            securityComponent.checkUserRightById(qr.get()::getParticipantId);
            QR gotQr = qr.get();
            Form form = new Form();
            form.setId(formId);
            gotQr.setForm(form);
            return update(gotQr);
        }
        throw new IllegalArgumentException("Not found QR with this id");
    }

    @CacheEvict(key = "#result.code", value = QR_CACHE_NAME)
    public QR update(QR qr) throws IllegalAccessException, JsonProcessingException {
        Optional<QR> gotQr = get(qr.getId());
        qr.setParticipantId(participantService.getCurrentSimpleOrThrow().getId());
        if (gotQr.isPresent()) {
            qr.setData(mergeDataAndGetString(gotQr.get().getData(), qr.getData()));
        }
        return qrRepository.save(qr);
    }

    public void delete(Long id) throws IllegalAccessException {
        get(id);
        qrRepository.deleteById(id);
    }

    @CacheEvict(key = "#result.code", value = QR_CACHE_NAME)
    public QRSimplestProjection uploadFile(Long id, FileUploadRequest fur) throws IllegalAccessException, IOException {
        QRSimplestProjection qr = get(id, QRSimplestProjection.class).get();
        FileProjection file = fileService.uploadFile(fur);
        qrRepository.insertFile(qr.getId(), file.getId());
        return get(id, QRSimplestProjection.class).get();
    }

    @CacheEvict(key = "#result.code", value = QR_CACHE_NAME)
    public QRSimplestProjection chooseFile(Long id, FileChooseRequest fcr) throws IllegalAccessException {
        get(id, QRSimplestProjection.class).get();
        qrRepository.insertFile(id, fcr.getId());
        return get(id, QRSimplestProjection.class).get();
    }

    @SneakyThrows
    @CacheEvict(key = "#result.code", value = QR_CACHE_NAME)
    public QRProjection deleteFile(Long id, Long fileId) {
        QRSimplestProjection qr = get(id, QRSimplestProjection.class).get();
        qrRepository.deleteFile(qr.getId(), fileId);
        return get(id, QRProjection.class).get();
    }

    private void downloadFiles(Long code, List<FileProjection> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files provided");
        }
        String tempDirPrefix = "QR-" + Long.toHexString(code);
        Path tempDirPath = Files.createTempDirectory(tempDirPrefix + " - ");
        try {
            HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            resp.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip");
            resp.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    String.format(
                            "attachment; filename*=UTF-8''" + tempDirPrefix + ".zip"
                    )
            );
            for (FileProjection fp : files) {
                if (fp.getIsPublic() && fp.getIsActive()) {
                    try (FileOutputStream fos
                                 = new FileOutputStream(new File(tempDirPath.toFile(), fp.getFileName()))) {
                        jdbcBlobProcessor.puller(fp.getId())
                                .accept(fos);
                    }
                }
            }

            ZipOutputStream zipOut = new ZipOutputStream(resp.getOutputStream());
            zipFile(tempDirPath.toFile(), tempDirPath.toFile().getName(), zipOut);
            zipOut.close();
        } catch (Exception e) {
            // ignore
        } finally {
            try {
                FileUtils.deleteDirectory(tempDirPath.toFile());
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public void annulForm(List<Long> ids) {
        if (ids == null) {
            return;
        }
        qrRepository.annulForm(ids);
    }

    private void checkUsedQr(Collection<QRRange> ranges, Collection<QR> used, Long qr) throws IllegalArgumentException {
        checkRanges(ranges);
        if (used == null) {
            throw new IllegalArgumentException("Used qrs are null");
        }
        for (QR us : used) {
            if (Objects.equals(us.getCode(), qr)) {
                throw new IllegalArgumentException("This qr already used");
            }
        }
        for (QRRange range : ranges) {
            if (qr > range.getTo() || qr < range.getFrom()) {
                throw new IllegalArgumentException("Illegal range for qr");
            }
        }
    }

    private Long getNextAvailableQR(List<QRRange> ranges, List<QR> used) throws IllegalArgumentException {
        checkRanges(ranges);
        ranges.sort(QRRange::compareTo);
        Long nextAvailableQR;
        Optional<Long> max = used.stream().map(QR::getCode).max(Long::compare);
        if (max.isPresent()) {
            nextAvailableQR = max.get() + 1;
        } else {
            nextAvailableQR = ranges.get(0).getFrom();
        }
        if (nextAvailableQR == null) {
            throw new IllegalArgumentException("Next qr value not found");
        }
        return nextAvailableQR;
    }

    private static void checkRanges(Collection<QRRange> ranges) {
        if (CollectionUtils.isEmpty(ranges)) {
            throw new IllegalArgumentException("Ranges are empty");
        }
    }

    private QRDto prepareDataBasedOnForm(QRProjection qr) throws JsonProcessingException {
        if (qr == null) {
            throw new IllegalArgumentException("QR is null");
        }
        QRDto dto = qrMapper.toDto(qr);
        FormComplexProjection form = qr.getForm();
        if (form == null) {
            dto.setData(EMPTY_JSON);
            return dto;
        }
        Form publicForm = new Form();

        List<FormField> formFields = form.getFields();

        if (formFields == null) {
            return dto;
        }

        // Map<Integer, List<FormField>> fieldsToRemove = new HashMap<>();
        for (int j = 0; j < formFields.size(); j++) {
            FormField field = formFields.get(j);
            Boolean isPublic = field.getIsPublic();
            if (isPublic != null && isPublic) {
                List<FormField> publicFields = publicForm.getFields();
                if (publicFields == null) {
                    publicForm.setFields(new ArrayList<>());
                }
                publicForm.getFields().add(field);
            }
        }

        List<FileProjection> formFiles = form.getFiles();
        if (formFiles != null) {
            if (publicForm.getFiles() == null) {
                publicForm.setFiles(new ArrayList<>());
            }
            for (FileProjection fp : formFiles) {
                Boolean isPublic = fp.getIsPublic();
                if (isPublic != null && isPublic) {
                    publicForm.getFiles().add(fileMapper.toEntity(fp));
                }
            }
        }

        dto.setForm(formMapper.toDto(publicForm));
        dto.setData(getDataBasedOnForm(qr));
        dto.setFiles(fileMapper.toListDto(getPublicFiles(qr.getFiles())));
        return dto;
    }

    private List<FileProjection> getPublicFiles(List<FileProjection> files) {
        if (files == null) {
            return Collections.emptyList();
        }
        return files.stream()
                .filter(file -> Boolean.TRUE.equals(file.getIsPublic()))
                .collect(Collectors.toList());
    }

    private String getDataBasedOnForm(QRProjection qr) throws JsonProcessingException {
        if (qr == null || qr.getForm() == null) {
            return EMPTY_JSON;
        }
        String qrData = qr.getData() == null ? EMPTY_JSON : qr.getData();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(qrData, new TypeReference<Map<String, Object>>() {
        });

        FormComplexProjection form = qr.getForm();

        List<FormField> formFields = form.getFields();

        if (formFields == null) {
            return EMPTY_JSON;
        }

        Set<String> dataToStand = new HashSet<>();
        for (FormField field : formFields) {
            if (field.getIsPublic() != null && field.getIsPublic()) {
                dataToStand.add(field.getName());
            }
        }
        Map<Object, Object> processedData =
                data.entrySet()
                        .stream().filter(entry -> dataToStand.contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (processedData.isEmpty()) {
            return EMPTY_JSON;
        }
        return mapper.writeValueAsString(processedData);
    }
}
