package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.mappers.FileMapper;
import org.eustrosoft.repositories.FileRepository;
import org.eustrosoft.repositories.projections.FileBytesProjection;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
    private final FileRepository repository;
    private final ParticipantService participantService;
    private final FileMapper mapper;
    private final SecurityComponent securityComponent;

    @SneakyThrows
    @Transactional(readOnly = true)
    public List<FileProjection> findAllMyFiles() {
        Participant participant = participantService.getCurrentOrThrow();
        return CommonUtils.iterableToList(
                repository.findAllByParticipantId(participant.getId(), FileProjection.class)
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
    public FileProjection uploadFile(FileUploadRequest fur) {
        File entity = mapper.toEntity(fur);
        return save(entity);
    }

    @SneakyThrows
    public FileProjection uploadFile(MultipartFile file) {
        File entity = mapper.toEntity(file.getOriginalFilename(), file);
        return save(entity);
    }

    @SneakyThrows
    private FileProjection save(File entity) {
        if (entity.getFileData() == null || entity.getFileData().length == 0) {
            throw new IllegalArgumentException("File is empty");
        }
        Participant current = participantService.getCurrentSimpleOrThrow();
        entity.setParticipantId(current.getId());
        return repository.save(entity);
    }

    @SneakyThrows
    public void delete(Long id) {
        FileProjection file = findById(id);
        securityComponent.checkUserRightById(file::getParticipantId);
        repository.deleteById(id);
    }

    public ResponseEntity<byte[]> downloadFile(Long id) {
        try {
            FileProjection file = repository.findById(id, FileProjection.class).get();
            if (file.getIsPublic() == null || file.getIsActive() == null) {
                throw new IllegalArgumentException("File is not public or inactive");
            }
            if (file.getIsPublic() && file.getIsActive()) {
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
        byte[] fileData = repository.findById(id, FileBytesProjection.class).get().getFileData();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", file.getFileType());
        headers.add("Content-Length", file.getFileSize().toString());
        headers.add(
                "Content-Disposition",
                String.format(
                        "inline; filename*=UTF-8''%s",
                        URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8.name())
                )
        );
        return new ResponseEntity<>(
                fileData, headers,
                HttpStatus.OK
        );
    }
}
