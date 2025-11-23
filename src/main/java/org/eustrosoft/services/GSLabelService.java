package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.eustrosoft.controllers.request.FileUploadRequest;
import org.eustrosoft.controllers.request.QRRequestFilter;
import org.eustrosoft.dtos.FileChooseRequest;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.entitites.Form;
import org.eustrosoft.entitites.FormField;
import org.eustrosoft.entitites.GSLabel;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.exceptions.CommonException;
import org.eustrosoft.exceptions.JsonApiError;
import org.eustrosoft.repositories.GSLabelRepository;
import org.eustrosoft.repositories.projections.EntityProjection;
import org.eustrosoft.repositories.projections.FileProjection;
import org.eustrosoft.repositories.projections.FormComplexProjection;
import org.eustrosoft.repositories.projections.QRProjection;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.eustrosoft.repositories.projections.QRSimplestProjection;
import org.eustrosoft.repositories.specifications.QRSpecifications;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.util.zip.ZipOutputStream;

import static org.eustrosoft.Constants.EMPTY_JSON;
import static org.eustrosoft.configurations.QRCachingConfig.QR_CACHE_NAME;
import static org.eustrosoft.utils.CommonUtils.mergeDataAndGetString;
import static org.eustrosoft.utils.CompressUtils.zipFile;

@Service
@RequiredArgsConstructor
@Transactional
public class GSLabelService {
    private final GSLabelRepository repository;
    private final ParticipantService participantService;
    private final SecurityComponent securityComponent;

    @Transactional(readOnly = true)
    public GSLabel get(Long id) throws IllegalAccessException {
        Optional<GSLabel> byId = repository.findById(id);
        securityComponent.checkUserRightById(byId.get()::getParticipantId);
        return byId.get();
    }

    @Transactional(readOnly = true)
    public List<GSLabel> findAllMine() throws IllegalAccessException {
        return CommonUtils.iterableToList(
                repository.findAllByParticipantIdOrderByCreatedDesc(
                        participantService.getCurrentSimpleOrThrow().getId()
                )
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GSLabel create(GSLabel gsLabel) throws IllegalAccessException, IllegalArgumentException {
        Participant current = participantService.getCurrentOrThrow();
        gsLabel.setParticipantId(current.getId());
        return repository.save(gsLabel);
    }

    public GSLabel update(GSLabel qr) throws IllegalAccessException, JsonProcessingException {
        GSLabel label = get(qr.getId());
        qr.setParticipantId(participantService.getCurrentSimpleOrThrow().getId());
        return repository.save(qr);
    }

    public void delete(Long id) throws IllegalAccessException {
        get(id);
        repository.deleteById(id);
    }
}
