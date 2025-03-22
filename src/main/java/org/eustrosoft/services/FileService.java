package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.controllers.request.FileReUploadRequest;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.controllers.request.FileWithBlobUploadRequest;
import org.eustrosoft.dtos.FileUploadResponse;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.FileBlob;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.enums.FileStorageType;
import org.eustrosoft.entitites.subentities.FileData;
import org.eustrosoft.exceptions.CommonException;
import org.eustrosoft.exceptions.JsonApiError;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.repositories.FileRepository;
import org.eustrosoft.repositories.projections.FileBytesProjection;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.repositories.sub.FileDataRepository;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.services.caches.QRCacheControlService;
import org.eustrosoft.utils.CommonUtils;
import org.eustrosoft.utils.JdbcBlobProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.eustrosoft.Constants.FIRST_ZLVL;
import static org.eustrosoft.Constants.FIRST_ZPID;
import static org.eustrosoft.Constants.FIRST_ZRID;
import static org.eustrosoft.Constants.FIRST_ZSTA;
import static org.eustrosoft.Constants.FIRST_ZTOV;
import static org.eustrosoft.Constants.FIRST_ZVER;

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

    @SneakyThrows
    public FileUploadResponse uploadFileWithBlob(FileWithBlobUploadRequest fur) {
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
            fuResp.setId(file.getId());
        } else {
            file.setParticipantId(current.getId());
            File saved = repository.save(file);
            qrCacheControlService.evictFromQrsCacheByFileId(current.getId(), saved.getId());

            fuResp.setId(saved.getId());

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
        if ((entity.getFileData() == null || entity.getFileData().length == 0)
                && StringUtils.isBlank(entity.getStoragePath())) {
            throw new IllegalArgumentException("Content could not be found");
        }
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
                id, entity.getFileData(), entity.getFileName(), entity.getFileType(),
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
        qrCacheControlService.evictFromQrsCacheByFileId(file.getParticipantId(), id);
        repository.deleteById(id);
    }

    public ResponseEntity<byte[]> downloadFile(Long id, String fileName) {
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
                return getFileResponse(id, file);
            }
            securityComponent.checkUserRightById(file::getParticipantId);
            return getFileResponse(id, file);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    private ResponseEntity<byte[]> getFileResponse(Long id, FileProjection file) {
        if (FileStorageType.URL.equals(file.getStoragePlace())
                && StringUtils.isNotBlank(file.getStoragePath())) {
            HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
                    .getResponse();
            // TODO: think about redirect page on js
            // response.setHeader("Redirect-url", file.getStoragePath());
            response.sendRedirect(file.getStoragePath());
            return ResponseEntity
                    .status(308)
                    .build();
        }
        byte[] fileData = getFileData(id);
        if (fileData == null) {
            HttpServletResponse response =
                    ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
                            .getResponse();
            long i = 1;

            try (OutputStream os = response.getOutputStream()) {
                while (true) {
                    i++;
                    response.setHeader(HttpHeaders.CONTENT_TYPE, file.getFileType());
                    response.setHeader(HttpHeaders.CONTENT_LENGTH, file.getFileSize().toString());
                    response.setHeader(
                            HttpHeaders.CONTENT_DISPOSITION,
                            String.format(
                                    "inline; filename*=UTF-8''%s",
                                    URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8.name())
                                            .replaceAll("\\+", "%20")
                            )
                    );
                    os.write(fileBlobService.getFileChunk(id, i).getChunk());
                }
            } catch (Exception e) {
                return null;
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, file.getFileType());
        headers.add(HttpHeaders.CONTENT_LENGTH, file.getFileSize().toString());
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                String.format(
                        "inline; filename*=UTF-8''%s",
                        URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8.name())
                                .replaceAll("\\+", "%20")
                )
        );
        return new ResponseEntity<>(
                fileData, headers,
                HttpStatus.OK
        );
    }

    @Transactional(readOnly = true)
    public byte[] getFileData(Long id) {
        return repository.findById(id, FileBytesProjection.class)
                .get().getFileData();
    }
}
