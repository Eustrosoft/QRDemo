package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.repositories.QRRepository;
import org.eustrosoft.repositories.projections.FileProjection;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.eustrosoft.Constants.EMPTY_JSON;
import static org.eustrosoft.Constants.MINIO_FILE_DIR_PATTERN;
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
    private final MinioService minioService;
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

    public QR getByCodePublic(Long code) throws JsonProcessingException {
        if (code == null) {
            throw new IllegalArgumentException("Illegal code");
        }
        Optional<QR> qr = qrRepository.findByCode(code);
        if (!qr.isPresent()) {
            throw new IllegalArgumentException("QR code not found");
        }
        QR gotQr = qr.get();
        gotQr.setName(null);
        gotQr.setDescription(null);
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
    public ResponseEntity<byte[]> getFileBytesResponse(Long id, String name) throws Exception {
        List<Result<Item>> files = minioService.listObjects(String.format(MINIO_FILE_DIR_PATTERN, id, name));
        if (files.isEmpty()) {
            throw new IllegalArgumentException("File not found");
        }
        String fileKey = files.stream().findFirst().get().get().objectName();
        StatObjectResponse metadata = minioService.getObjectMetadata(fileKey);
        byte[] objectBytes = minioService.getObjectBytes(fileKey);
        String fileLengthInBytes = String.valueOf(objectBytes.length);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, metadata.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format(
                                "attachment; filename*=UTF-8''%s",
                                URLEncoder.encode(name, StandardCharsets.UTF_8.name())
                        )
                )
                .header(HttpHeaders.ACCEPT_RANGES, "Bytes")
                .header(HttpHeaders.CONTENT_RANGE, "Bytes" + " " + 0 + "-" + fileLengthInBytes + "/" + fileLengthInBytes)
                .header(HttpHeaders.CONTENT_LENGTH, fileLengthInBytes)
                .body(objectBytes);
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

    private QR prepareDataBasedOnForm(QR qr) throws JsonProcessingException {
        if (qr == null) {
            throw new IllegalArgumentException("QR is null");
        }
        Form form = qr.getForm();
        if (form == null) {
            qr.setData(EMPTY_JSON);
            return qr;
        }
        if (StringUtils.isEmpty(qr.getData())) {
            form = new Form();
            return qr;
        }
        Form publicForm = new Form();

        List<FormField> formFields = form.getFields();

        if (formFields == null) {
            return qr;
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

        qr.setForm(publicForm);
        qr.setData(getDataBasedOnForm(qr));
        return qr;
    }

    private String getDataBasedOnForm(QR qr) throws JsonProcessingException {
        if (qr == null || qr.getForm() == null || qr.getData() == null) {
            return EMPTY_JSON;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(qr.getData(), new TypeReference<Map<String, Object>>() {
        });

        Form form = qr.getForm();

        List<FormField> formFields = form.getFields();

        if (formFields == null) {
            return EMPTY_JSON;
        }

        Set<String> dataToStand = new HashSet<>();
        for (FormField field : formFields) {
            dataToStand.add(field.getName());
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
