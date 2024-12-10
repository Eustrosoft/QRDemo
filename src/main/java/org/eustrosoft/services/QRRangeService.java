package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.repositories.QRRangeRepository;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.eustrosoft.Constants.CODES_FOR_RANGE;
import static org.eustrosoft.Constants.RANGE_END;
import static org.eustrosoft.Constants.RANGE_START;

@Service
@RequiredArgsConstructor
public class QRRangeService {
    private final QRRangeRepository repository;
    private final ParticipantService participantService;

    public QRRange getQRRange(Long id) {
        return repository.findById(id).get();
    }

    public Collection<QRRange> getMyRanges() throws IllegalAccessException {
        Participant current = participantService.getCurrentSimpleOrThrow();
        Participant participant = participantService.getById(current.getId());
        if (participant == null) {
            return Collections.emptyList();
        }
        return participant.getRanges();
    }

    public List<QRRange> findAll() {
        return CommonUtils.iterableToList(repository.findAll());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public QRRange generateNextRange() throws IllegalArgumentException {
        List<QRRange> freeRanges = getFreeRanges();
        if (freeRanges == null || freeRanges.isEmpty()) {
            throw new IllegalArgumentException("No free ranges found");
        }
        QRRange foundRange = null;
        for (QRRange qrRange : freeRanges) {
            long codesSize = qrRange.getTo() - qrRange.getFrom();
            if (codesSize < CODES_FOR_RANGE) {
                continue;
            } else {
                foundRange = new QRRange();
                foundRange.setFrom(qrRange.getFrom());
                foundRange.setTo(qrRange.getFrom() + CODES_FOR_RANGE);
            }
        }
        if (foundRange == null) {
            throw new IllegalArgumentException("No free ranges found");
        }
        return foundRange;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public QRRange create(QRRange qrRange) {
        long codes = qrRange.getTo() - qrRange.getFrom();
        if (codes != CODES_FOR_RANGE) {
            throw new IllegalArgumentException("Not 16 codes for range");
        }
        List<QRRange> existed = findAll();
        boolean canCreate = canCreateQRRange(existed, qrRange);
        if (!canCreate) {
            throw new IllegalArgumentException("Used qr codes are in range");
        }
        return repository.save(qrRange);
    }

    @Transactional
    public QRRange update(QRRange qrRange) {
        return repository.save(qrRange);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<QRRange> getFreeRanges() {
        List<QRRange> ranges = findAll();

        List<Long> usedQrs = new ArrayList<>();
        for (QRRange range : ranges) {
            usedQrs.addAll(getQrsInRange(range));
        }
        List<QRRange> unusedRanges = new ArrayList<>();
        long unused = 0L;
        for (long i = RANGE_START; i <= RANGE_END; i++) {
            if (!usedQrs.contains(i)) {
                unused++;
            } else {
                if (unused != 0L) {
                    QRRange unusedRange = new QRRange();
                    unusedRange.setFrom(i - unused + 1);
                    unusedRange.setTo(i);
                    unusedRanges.add(unusedRange);
                }
                unused = 0L;
            }
            if (unused != 0L && i == RANGE_END) {
                QRRange unusedRange = new QRRange();
                unusedRange.setFrom(i - unused + 1);
                unusedRange.setTo(i);
                unusedRanges.add(unusedRange);
            }
        }
        return unusedRanges;
    }

    public boolean canCreateQRRange(List<QRRange> ranges, QRRange qrRange) {
        Long from = qrRange.getFrom();
        Long to = qrRange.getTo();
        if (from == null || to == null) {
            return false;
        }
        if (from < RANGE_START || to > RANGE_END) {
            return false;
        }
        if (ranges == null || ranges.isEmpty()) {
            return true;
        }
        List<Long> usedQrs = new ArrayList<>();
        for (QRRange range : ranges) {
            usedQrs.addAll(getQrsInRange(range));
        }
        return !(usedQrs.contains(from) || usedQrs.contains(to));
    }

    private List<Long> getQrsInRange(QRRange qrRange) {
        if (qrRange == null || qrRange.getFrom() == null || qrRange.getTo() == null) {
            return Collections.emptyList();
        }
        List<Long> qrs = new ArrayList<>();
        for (long i = qrRange.getFrom(); i <= qrRange.getTo(); i++) {
            qrs.add(i);
        }
        return qrs;
    }
}
