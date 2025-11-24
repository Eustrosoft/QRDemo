package org.eustrosoft.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.controllers.request.GSLabelsRequest;
import org.eustrosoft.entitites.GSLabel;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.GSLabelRepository;
import org.eustrosoft.security.SecurityComponent;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public List<GSLabel> findAllMine(GSLabelsRequest request) throws IllegalAccessException {
        Long qrId = request.getQrId();
        if (qrId != null) {
            return CommonUtils.iterableToList(
                    repository.findAllByParticipantIdAndQrIdOrderByCreatedDesc(
                            participantService.getCurrentSimpleOrThrow().getId(),
                            qrId
                    )
            );
        }
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
