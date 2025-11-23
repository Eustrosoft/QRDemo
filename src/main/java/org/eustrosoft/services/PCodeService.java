package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.PCode;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.PCodeRepository;
import org.eustrosoft.security.SecurityComponent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.eustrosoft.utils.CommonUtils.iterableToList;

@Service
@RequiredArgsConstructor
@Transactional
public class PCodeService {
    private final PCodeRepository repository;
    private final ParticipantService participantService;
    private final SecurityComponent securityComponent;

    @Transactional(readOnly = true)
    public PCode get(Long docId, Long rowId) throws IllegalAccessException {
        Optional<PCode> pCode = repository.findByDocIdAndRowId(docId, rowId);
        securityComponent.checkUserRightById(pCode.get()::getParticipantId);
        return pCode.get();
    }

    @Transactional(readOnly = true)
    public List<PCode> findAllMine() throws IllegalAccessException {
        Long participantId = participantService.getCurrentSimpleOrThrow().getId();
        return iterableToList(
                repository.findAllByParticipantId(participantId)
        );
    }

    @Transactional(readOnly = true)
    public PCode create(PCode pCode) throws IllegalAccessException {
        Participant current = participantService.getCurrentOrThrow();
        pCode.setParticipantId(current.getId());
        return repository.save(pCode);
    }

    @Transactional(readOnly = true)
    public PCode update(PCode pCode) throws IllegalAccessException {
        get(pCode.getDocId(), pCode.getRowId());
        return repository.save(pCode);
    }

    @Transactional
    public void delete(Long docId, Long rowId) throws IllegalAccessException {
        get(docId, rowId);
        repository.deleteByDocIdAndRowId(docId, rowId);
    }
}
