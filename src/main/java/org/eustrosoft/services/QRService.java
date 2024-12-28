package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.mappers.FormMapper;
import org.eustrosoft.mappers.QrMapper;
import org.eustrosoft.repositories.QRRepository;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.repositories.projections.FormComplexProjection;
import org.eustrosoft.repositories.projections.QRProjection;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

import static org.eustrosoft.Constants.EMPTY_JSON;
import static org.eustrosoft.Constants.RANGE_END;
import static org.eustrosoft.Constants.RANGE_START;
import static org.eustrosoft.utils.CommonUtils.mergeDataAndGetString;
import static org.eustrosoft.utils.FileUtils.getFileIndex;

@Service
@RequiredArgsConstructor
public class QRService {
    private final QRRepository qrRepository;
    private final ParticipantService participantService;
    private final SecurityComponent securityComponent;
    private final QrMapper qrMapper;
    private final FormMapper formMapper;
    private final FileMapper fileMapper;
    private final FileService fileService;

    public Optional<QR> get(Long id) throws IllegalAccessException {
        Optional<QR> byId = qrRepository.findById(id);
        securityComponent.checkUserRightById(byId.get()::getParticipantId);
        return byId;
    }

    public Optional<QR> getByCode(Long code) throws IllegalAccessException {
        Optional<QR> byId = qrRepository.findByCode(code);
        securityComponent.checkUserRightById(byId.get()::getParticipantId);
        return qrRepository.findByCode(code);
    }

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

    public List<QRSimpleProjection> findAllMine() throws IllegalAccessException {
        return CommonUtils.iterableToList(
                qrRepository.findAllByParticipantIdOrderByUpdatedDesc(
                        participantService.getCurrentSimpleOrThrow().getId()
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
        Long code = qr.getCode();
        if (code < RANGE_START || code > RANGE_END) {
            throw new IllegalArgumentException("Code has illegal character");
        }
        qr.setParticipantId(current.getId());
        return qrRepository.save(qr);
    }

    @Transactional
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

    @Transactional
    public QR update(QR qr) throws IllegalAccessException, JsonProcessingException {
        Optional<QR> gotQr = get(qr.getId());
        qr.setParticipantId(participantService.getCurrentSimpleOrThrow().getId());
        if (gotQr.isPresent()) {
            qr.setData(mergeDataAndGetString(gotQr.get().getData(), qr.getData()));
        }
        return qrRepository.save(qr);
    }

    @Transactional
    public void delete(Long id) throws IllegalAccessException {
        get(id);
        qrRepository.deleteById(id);
    }

    @Transactional
    public FileProjection uploadFile(Long id, FileUploadRequest fur) throws IllegalAccessException, IOException {
        QR qr = get(id).get();
        FileProjection file = fileService.uploadFile(fur);
        List<File> files = qr.getFiles();
        if (files == null) {
            qr.setFiles(new ArrayList<>());
        }
        qr.getFiles().add(new File(file.getId()));
        update(qr);
        return file;
    }

    @Transactional
    @SneakyThrows
    public void deleteFile(Long id, Long fileId) {
        QR qr = get(id).get();
        List<File> files = qr.getFiles();
        if (files == null) {
            throw new IllegalArgumentException("There are no files in this qr");
        }
        int index = getFileIndex(fileId, files);
        files.remove(index);
        update(qr);
    }

    @Transactional
    public ResponseEntity<byte[]> getFileBytesResponseV2(Long id) throws Exception {
        FileProjection file = fileService.findById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, file.getFileType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format(
                                "attachment; filename*=UTF-8''%s",
                                URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8.name())
                        )
                )
                .header(HttpHeaders.ACCEPT_RANGES, "Bytes")
                .header(HttpHeaders.CONTENT_RANGE, "Bytes" + " " + 0 + "-" + file.getFileSize() + "/" + file.getFileSize())
                .header(HttpHeaders.CONTENT_LENGTH, file.getFileSize().toString())
                .body(fileService.getFileBytes(id));
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

    private Long getNextAvailableQR(Collection<QRRange> ranges, Collection<QR> used) throws IllegalArgumentException {
        checkRanges(ranges);
        Long nextAvailableQR = null;
        List<Long> usedQRs = new ArrayList<>(used.stream().map(QR::getCode).collect(Collectors.toList()));
        for (QRRange range : ranges) {
            for (long i = range.getFrom(); i <= range.getTo(); i++) {
                if (usedQRs.contains(i)) {
                    continue;
                }
                nextAvailableQR = i;
                break;
            }
            if (nextAvailableQR != null) {
                break;
            }
        }
        if (nextAvailableQR == null) {
            throw new IllegalArgumentException("Next qr value not found");
        }
        return nextAvailableQR;
    }

    private static void checkRanges(Collection<QRRange> ranges) {
        if (ranges == null || ranges.isEmpty()) {
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
