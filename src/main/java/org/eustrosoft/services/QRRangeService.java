package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.configurations.QRRangeConfig;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.repositories.QRRangeRepository;
import org.eustrosoft.repositories.QRRepository;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.eustrosoft.Constants.QRDEMO;

@Service
@Transactional
@RequiredArgsConstructor
public class QRRangeService {
    private final QRRangeConfig qrRangeConfig;
    private final QRRangeRepository repository;
    private final ParticipantService participantService;

    @Transactional(readOnly = true)
    public QRRange getQRRange(Long id) {
        return repository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public Collection<QRRange> getMyRanges() throws IllegalAccessException {
        Participant participant = participantService.findById(
                participantService.getCurrentSimpleOrThrow().getId()
        );
        if (participant == null) {
            throw new IllegalArgumentException("Participant not found");
        }
        return participant.getRanges();
    }

    @Transactional(readOnly = true)
    public List<QRRange> findAll() {
        return CommonUtils.iterableToList(repository.findAll());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QRRange generateNextRange() throws IllegalArgumentException {
        QRRange foundRange = null;
        Long rangeStart = repository.nextQRange(QRDEMO);
        if (rangeStart == null) {
            throw new IllegalArgumentException("No free ranges found");
        } else {
            foundRange = new QRRange();
            foundRange.setFrom(rangeStart);
            foundRange.setTo(rangeStart + qrRangeConfig.getCodesForRange());
        }
        if (foundRange == null) {
            throw new IllegalArgumentException("No free ranges found");
        }
        return foundRange;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QRRange create(QRRange qrRange) {
        long codes = qrRange.getTo() - qrRange.getFrom();
        if (codes != qrRangeConfig.getCodesForRange()) {
            throw new IllegalArgumentException("Not 16 codes for range");
        }
//        List<QRRange> existed = findAll();
        // TODO: added ability to create any range
//        boolean canCreate = canCreateQRRange(existed, qrRange);
//        if (!canCreate) {
//            throw new IllegalArgumentException("Used qr codes are in range");
//        }
        return repository.save(qrRange);
    }

    public QRRange update(QRRange qrRange) {
        return repository.save(qrRange);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<QRRange> getFreeRanges() {
        List<QRRange> ranges = findAll();
        List<QRRange> unusedRanges = new ArrayList<>();
        long rangeToUse = 0L;
        for (long i = qrRangeConfig.getRangeStart(); i <= qrRangeConfig.getRangeEnd(); i++) {

        }
        return unusedRanges;
    }

    public boolean canCreateQRRange(List<QRRange> ranges, QRRange qrRange) {
        Long from = qrRange.getFrom();
        Long to = qrRange.getTo();
        if (from == null || to == null) {
            return false;
        }
        if (from < qrRangeConfig.getRangeStart() || to > qrRangeConfig.getRangeEnd()) {
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
