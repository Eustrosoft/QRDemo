package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.FormBlock;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.repositories.QRRepository;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.eustrosoft.Constants.EMPTY_JSON;
import static org.eustrosoft.Constants.MINIO_FILES_PATTERN;
import static org.eustrosoft.Constants.MINIO_FILE_DIR_PATTERN;
import static org.eustrosoft.Constants.RANGE_END;
import static org.eustrosoft.Constants.RANGE_START;
import static org.eustrosoft.utils.CommonUtils.mergeDataAndGetString;

@Service
@RequiredArgsConstructor
public class QRService {
    private final QRRepository qrRepository;
    private final ParticipantService participantService;
    private final SecurityComponent securityComponent;
    private final MinioService minioService;
    private final ObjectMapper mapper;

    public Optional<QR> get(Long id) throws IllegalAccessException {
        Optional<QR> byId = qrRepository.findById(id);
        securityComponent.checkUserRight(byId.get()::getParticipant);
        return byId;
    }

    public Optional<QR> getByCode(Long code) throws IllegalAccessException {
        Optional<QR> byId = qrRepository.findByCode(code);
        securityComponent.checkUserRight(byId.get()::getParticipant);
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
                qrRepository.findAllByParticipantOrderByUpdatedDesc(participantService.getCurrentSimpleOrThrow())
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
        qr.setParticipant(current);
        return qrRepository.save(qr);
    }

    @Transactional
    public QR setFormForQR(Long id, Long formId) throws IllegalAccessException, JsonProcessingException {
        Optional<QR> qr = get(id);
        if (qr.isPresent()) {
            securityComponent.checkUserRight(qr.get()::getParticipant);
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
        qr.setParticipant(participantService.getCurrentSimpleOrThrow());
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
    public void uploadFile(Long id, String name, MultipartFile file) throws IllegalAccessException, IOException {
        QR qr = get(id).get();
        minioService.deleteAllFilesInDirectory(String.format(MINIO_FILE_DIR_PATTERN, qr.getId(), name));
        minioService.putObject(
                String.format(MINIO_FILES_PATTERN, qr.getId(), name, file.getOriginalFilename()),
                file.getInputStream(),
                file.getContentType()
        );
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
        List<FormBlock> blocks = form.getBlocks();
        if (blocks == null || blocks.isEmpty()) {
            return qr;
        }
        Form publicForm = new Form();

        // Map<Integer, List<FormField>> fieldsToRemove = new HashMap<>();
        for (int i = 0; i < blocks.size(); i++) {
            FormBlock block = blocks.get(i);
            List<FormField> fields = block.getFields();
            if (fields == null || fields.isEmpty()) {
                continue;
            }
            FormBlock publicBlock = new FormBlock();
            publicBlock.setName(block.getName());
            publicBlock.setDescription(block.getDescription());
            if (publicForm.getBlocks() == null) {
                publicForm.setBlocks(new ArrayList<>());
            }
            publicForm.getBlocks().add(publicBlock);

            for (int j = 0; j < fields.size(); j++) {
                FormField field = fields.get(j);
                Boolean isPublic = field.getIsPublic();
                if (isPublic != null && isPublic) {
                    List<FormField> pubFields = publicBlock.getFields();
                    if (pubFields == null) {
                        publicBlock.setFields(new ArrayList<>());
                    }
                    publicBlock.getFields().add(field);
                }
            }
        }
        qr.setForm(publicForm);
        qr.setData(getDataBasedOnForm(qr));
        return qr;
    }

    private void removeFieldsBasedOnMap(Form form, Map<Integer, List<FormField>> fieldsToRemove) {
        if (form == null || fieldsToRemove == null || fieldsToRemove.isEmpty()) {
            return;
        }
        List<FormBlock> blocks = form.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            if (!fieldsToRemove.containsKey(i)) {
                continue;
            }
            List<FormField> fields = blocks.get(i).getFields();
            if (fields == null || fields.isEmpty()) {
                continue;
            }
            List<FormField> toRemove = fieldsToRemove.get(i);
            fields.removeAll(toRemove);
        }
    }

    private String getDataBasedOnForm(QR qr) throws JsonProcessingException {
        if (qr == null || qr.getForm() == null || qr.getData() == null) {
            return EMPTY_JSON;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(qr.getData(), new TypeReference<Map<String, Object>>() {
        });

        Form form = qr.getForm();
        List<FormBlock> blocks = form.getBlocks();
        Set<String> dataToStand = new HashSet<>();
        for (int i = 0; i < blocks.size(); i++) {
            FormBlock block = blocks.get(i);
            List<FormField> fields = block.getFields();
            if (fields == null || fields.isEmpty()) {
                continue;
            }
            for (int j = 0; j < fields.size(); j++) {
                FormField field = fields.get(j);
                dataToStand.add(field.getName());
            }
        }
        Map<Object, Object> processedData =
                data.entrySet()
                        .stream().filter(entry -> dataToStand.contains(entry.getKey()))
                        .collect(Collectors.toMap(v -> v.getKey(), v1 -> v1.getValue()));
        if (processedData.isEmpty()) {
            return EMPTY_JSON;
        }
        return mapper.writeValueAsString(processedData);
    }
}
