package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.controllers.request.FileReUploadRequest;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.controllers.request.FileWithBlobUploadRequest;
import org.eustrosoft.controllers.request.RelatedType;
import org.eustrosoft.dtos.FileUploadResponse;
import org.eustrosoft.entitites.Dictionary;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.FileBlob;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.enums.FileStorageType;
import org.eustrosoft.entitites.subentities.FileData;
import org.eustrosoft.exceptions.CommonException;
import org.eustrosoft.exceptions.JsonApiError;
import org.eustrosoft.exceptions.custom.IllegalActionException;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.repositories.FileRepository;
import org.eustrosoft.repositories.projections.EntityProjection;
import org.eustrosoft.repositories.projections.FileComplexProjection;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.repositories.projections.FormQrsProjection;
import org.eustrosoft.repositories.sub.FileDataRepository;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.services.caches.QRCacheControlService;
import org.eustrosoft.utils.CommonUtils;
import org.eustrosoft.utils.JdbcBlobProcessor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.eustrosoft.Constants.Dictionary.CODE_CHUNK_SIZE;
import static org.eustrosoft.Constants.Dictionary.CODE_DOWNLOAD_ALLOWED_MIME_TYPE;
import static org.eustrosoft.Constants.Dictionary.NAME_CHUNK_SIZE;
import static org.eustrosoft.Constants.FIRST_ZLVL;
import static org.eustrosoft.Constants.FIRST_ZPID;
import static org.eustrosoft.Constants.FIRST_ZRID;
import static org.eustrosoft.Constants.FIRST_ZSTA;
import static org.eustrosoft.Constants.FIRST_ZTOV;
import static org.eustrosoft.Constants.FIRST_ZVER;
import static org.eustrosoft.Constants.MAXIMUM_CHUNK_SIZE;
import static org.eustrosoft.Constants.Properties.DEFAULT_MAXIMUM_CHUNKS;
import static org.eustrosoft.Constants.Properties.MAXIMUM_CHUNKS;
import static org.eustrosoft.utils.HttpUtils.getASCIIUrl;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
    private final FileRepository repository;
    private final FileDataRepository fileDataRepository;
    private final ParticipantService participantService;
    private final FileMapper mapper;
    private final SecurityComponent securityComponent;
    private final QRCacheControlService qrCacheControlService;
    private final FileBlobService fileBlobService;
    private final JdbcBlobProcessor jdbcBlobProcessor;
    private final Environment environment;
    private final DictionaryService dictionaryService;

    @SneakyThrows
    @Transactional(readOnly = true)
    public List<FileProjection> findAllMyFiles() {
        Participant participant = participantService.getCurrentOrThrow();
        return CommonUtils.iterableToList(
                repository.findAllByParticipantIdOrderByUpdatedDesc(participant.getId(), FileProjection.class)
        );
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public FileProjection findById(Long id) {
        Participant current = participantService.getCurrentSimpleOrThrow();
        FileProjection file = repository.findById(id, FileProjection.class).get();
        if (file.getIsPublic() || file.getParticipantId().equals(current.getId())) {
            return file;
        }
        throw new IllegalAccessException("File is not public or yours");
    }

    @Transactional(readOnly = true)
    public List<EntityProjection> getRelated(Long id) throws IllegalAccessException {
        return getRelated(id, null);
    }

    @Transactional(readOnly = true)
    public List<EntityProjection> getRelated(Long id, RelatedType type) throws IllegalAccessException {
        FileComplexProjection file = repository.findById(id, FileComplexProjection.class).get();
        List<EntityProjection> entities = new ArrayList<>();
        if (type == null) { // TODO: think of better
            entities.addAll(file.getQrs());
            entities.addAll(file.getForms());
        } else if (type == RelatedType.FM) {
            entities.addAll(file.getForms());
        } else if (type == RelatedType.QR) {
            entities.addAll(file.getQrs());
        }
        return entities;
    }

    @SneakyThrows
    public FileUploadResponse uploadFileWithBlob(FileWithBlobUploadRequest fur) {
        if (FileStorageType.DB != fur.getFileStorageType()) {
            FileProjection projection = uploadFile(
                    new FileUploadRequest(
                            fur.getName(), fur.getDescription(),
                            null, fur.getStoragePath(), fur.getFileStorageType(),
                            fur.isPublic(), fur.isActive()
                    )
            );
            FileUploadResponse fileUploadResponse = new FileUploadResponse();
            fileUploadResponse.setFileId(projection.getId());
            return fileUploadResponse;
        }
        MultipartFile chunk = fur.getChunk();
        Integer maximumChunks = getMaximumChunks();
        if (fur.getTotal() > maximumChunks) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST, 400L,
                            "exceptions.title.file_upload_exception",
                            "exceptions.detail.file_upload_exception",
                            new JsonApiError.Source("totalChunk"),
                            maximumChunks
                    )
            );
        }
        if (chunk.getSize() > getMaximumChunkSize()) {
            throw new IllegalArgumentException("Maximum chunks are exceed");
        }
        Map.Entry<File, FileBlob> fileMap = mapper.toFileMap(fur);
        if (fileMap == null) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST,
                            "exceptions.title.unprocessable_entity",
                            "exceptions.detail.unprocessable_entity"
                    )
            );
        }
        File file = fileMap.getKey();
        FileBlob blob = fileMap.getValue();

        Participant current = participantService.getCurrentSimpleOrThrow();

        FileUploadResponse fuResp = new FileUploadResponse();

        Timestamp zdate = new Timestamp(System.currentTimeMillis());
        if (file.getId() != null) {
            findById(file.getId());

            blob.setZOID(file.getId());
            blob.setZRID(fur.getNo());
            blob.setZVER(FIRST_ZVER);
            blob.setZLVL(FIRST_ZLVL);
            blob.setZPID(FIRST_ZPID);
            blob.setZTOV(FIRST_ZTOV);
            blob.setZSTA(FIRST_ZSTA);
            blob.setZSID(current.getId());
            blob.setZUID(current.getId());
            blob.setZUIDO(current.getId());
            blob.setZDATE(zdate);
            blob.setZDATO(zdate);
            fileBlobService.save(blob);
            fuResp.setFileId(file.getId());
        } else {
            file.setParticipantId(current.getId());
            File saved = repository.save(file);
            qrCacheControlService.evictFromQrsCacheByFileId(current.getId(), saved.getId());

            fuResp.setFileId(saved.getId());

            blob.setZOID(saved.getId());
            blob.setZRID(FIRST_ZRID);
            blob.setZVER(FIRST_ZVER);
            blob.setZLVL(FIRST_ZLVL);
            blob.setZPID(FIRST_ZPID);
            blob.setZTOV(FIRST_ZTOV);
            blob.setZSTA(FIRST_ZSTA);
            blob.setZSID(current.getId());
            blob.setZUID(current.getId());
            blob.setZUIDO(current.getId());
            blob.setZDATE(zdate);
            blob.setZDATO(zdate);
            fileBlobService.save(blob);
        }

        fuResp.setNo(blob.getNo());
        return fuResp;
    }

    @SneakyThrows
    public FileProjection uploadFile(FileUploadRequest fur) {
        File entity = mapper.toEntity(fur);
        if (entity == null) {
            throw new CommonException(
                    new JsonApiError(
                            HttpStatus.BAD_REQUEST,
                            "exceptions.title.unprocessable_entity",
                            "exceptions.detail.unprocessable_entity"
                    )
            );
        }
        return save(entity);
    }

    @SneakyThrows
    public FileProjection uploadFile(MultipartFile file) {
        File entity = mapper.toEntity(file.getOriginalFilename(), file);
        return save(entity);
    }

    @SneakyThrows
    private FileProjection save(File entity) {
        Participant current = participantService.getCurrentSimpleOrThrow();
        entity.setParticipantId(current.getId());
        File saved = repository.save(entity);
        qrCacheControlService.evictFromQrsCacheByFileId(current.getId(), saved.getId());
        return saved;
    }

    @SneakyThrows
    public FileProjection changeFile(Long id, FileReUploadRequest fur) {
        FileProjection byId = findById(id);
        securityComponent.checkUserRightById(byId::getParticipantId);
        File entity = mapper.toEntity(fur);
        repository.updateFileData(
                id, entity.getFileName(), entity.getFileType(),
                entity.getExtension(), entity.getChecksum(), entity.getFileSize()
        );
        qrCacheControlService.evictFromQrsCacheByFileId(byId.getParticipantId(), id);
        return byId;
    }

    @SneakyThrows
    public FileProjection update(FileData fileData) {
        FileProjection byId = findById(fileData.getId());
        securityComponent.checkUserRightById(byId::getParticipantId);
        fileDataRepository.save(fileData);
        qrCacheControlService.evictFromQrsCacheByFileId(byId.getParticipantId(), fileData.getId());
        return findById(fileData.getId());
    }

    @SneakyThrows
    public void delete(Long id) {
        FileProjection file = findById(id);
        securityComponent.checkUserRightById(file::getParticipantId);
        checkFileHasNoConnections(id);
        qrCacheControlService.evictFromQrsCacheByFileId(file.getParticipantId(), id);
        repository.deleteById(id);
    }

    public void downloadFile(Long id, String fileName) {
        try {
            FileProjection file = repository.findById(id, FileProjection.class).get();
            if (file.getIsPublic() == null || file.getIsActive() == null) {
                throw new IllegalArgumentException("File is not public or inactive");
            }
            if (FileStorageType.DB.equals(file.getStoragePlace())
                    && (StringUtils.isEmpty(fileName) || !file.getFileName().equals(fileName))) {
                throw new IllegalArgumentException("File name is not correct");
            }
            if (file.getIsActive()) {
                getFileResponse(id, file);
            }
            securityComponent.checkUserRightById(file::getParticipantId);
            getFileResponse(id, file);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Request is forbidden");
        }
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    private void getFileResponse(Long id, FileProjection file) {
        if (FileStorageType.URL.equals(file.getStoragePlace())
                && StringUtils.isNotBlank(file.getStoragePath())) {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getResponse();
            // TODO: think about redirect page on js
            // response.setHeader("Redirect-url", file.getStoragePath());
            response.sendRedirect(getASCIIUrl(file.getStoragePath()));
            return;
        }

        HttpServletResponse response =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getResponse();
        long i = 1;

        List<String> allowedMimeTypes = dictionaryService.getDictionariesByCode(CODE_DOWNLOAD_ALLOWED_MIME_TYPE)
                .stream().map(Dictionary::getValue).collect(Collectors.toList());

        try (OutputStream os = response.getOutputStream()) {
            if (allowedMimeTypes.contains(file.getFileType())) {
                response.setHeader(HttpHeaders.CONTENT_TYPE, file.getFileType());
            } else {
                response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            }
            response.setHeader(HttpHeaders.CONTENT_LENGTH, file.getFileSize().toString());
            response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    String.format(
                            "inline; filename*=UTF-8''%s",
                            URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8.name())
                                    .replaceAll("\\+", "%20")
                    )
            );
            while (true) {
                os.write(fileBlobService.getFileChunk(id, i).getChunk());
                i++;
            }
        } catch (Exception e) {
            // ignore, file was fully downloaded
        }
    }

    @Transactional(readOnly = true)
    private void checkFileHasNoConnections(Long id) {
        Integer relatedQRs = repository.countRelatedQRs(id);
        if (relatedQRs != null && relatedQRs > 0) {
            throw new IllegalActionException(
                    new JsonApiError(
                            HttpStatus.CONFLICT,
                            "exceptions.title.delete_linked_file",
                            "exceptions.detail.delete_linked_file"
                    )
            );
        }
        Integer relatedFiles = repository.countRelatedForms(id);
        if (relatedFiles != null && relatedFiles > 0) {
            throw new IllegalActionException(
                    new JsonApiError(
                            HttpStatus.CONFLICT,
                            "exceptions.title.delete_linked_file",
                            "exceptions.detail.delete_linked_file"
                    )
            );
        }
    }

    private Integer getMaximumChunks() {
        try {
            String property = environment.getProperty(MAXIMUM_CHUNKS);
            if (property == null) {
                throw new IllegalArgumentException("Property maximum chunks can not be found in application properties");
            }
            return Integer.parseInt(property);
        } catch (Exception e) {
            return DEFAULT_MAXIMUM_CHUNKS;
        }
    }

    private Integer getMaximumChunkSize() {
        try {
            Dictionary sizeDic = dictionaryService.findByCodeAndName(CODE_CHUNK_SIZE, NAME_CHUNK_SIZE);
            return Integer.parseInt(sizeDic.getValue());
        } catch (Exception e) {
            return MAXIMUM_CHUNK_SIZE;
        }
    }
}
