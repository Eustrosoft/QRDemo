package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.MRange;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.repositories.MRangeRepository;
import org.eustrosoft.security.SecurityComponent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.eustrosoft.utils.CommonUtils.iterableToList;

@Service
@RequiredArgsConstructor
@Transactional
public class MRangeService {
    private final MRangeRepository repository;
    private final ParticipantService participantService;
    private final SecurityComponent securityComponent;

    @Transactional(readOnly = true)
    public MRange get(Long id) throws IllegalAccessException {
        Optional<MRange> mRange = repository.findById(id);
        securityComponent.checkUserRightById(mRange.get()::getParticipantId);
        return mRange.get();
    }

    @Transactional(readOnly = true)
    public List<MRange> findAllMine() throws IllegalAccessException {
        Long participantId = participantService.getCurrentSimpleOrThrow().getId();
        return iterableToList(
                repository.findAllByParticipantIdOrderByCreatedDesc(participantId)
        );
    }

    @Transactional(readOnly = true)
    public MRange create(MRange mRange) throws IllegalAccessException {
        Participant current = participantService.getCurrentOrThrow();
        mRange.setParticipantId(current.getId());
        return repository.save(mRange);
    }

    @Transactional(readOnly = true)
    public MRange update(MRange mRange) throws IllegalAccessException {
        get(mRange.getId());
        return repository.save(mRange);
    }

    @Transactional
    public void delete(Long id) throws IllegalAccessException {
        get(id);
        repository.deleteById(id);
    }
}
