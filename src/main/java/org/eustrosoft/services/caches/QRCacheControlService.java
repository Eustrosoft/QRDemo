package org.eustrosoft.services.caches;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.repositories.QRRepository;
import org.eustrosoft.repositories.projections.EntityProjection;
import org.eustrosoft.repositories.projections.QRSimplestProjection;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.eustrosoft.configurations.QRCachingConfig.QR_CACHE_NAME;

@Service
@Transactional
@RequiredArgsConstructor
public class QRCacheControlService {
    private final QRRepository qrRepository;
    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    public <T extends EntityProjection> List<T> findAllByFormIdAndParticipantId(
            Long participantId, Long formId, Class<T> clazz
    ) {
        return CommonUtils.iterableToList(
                qrRepository.findAllByParticipantIdAndFormId(
                        participantId,
                        formId,
                        clazz
                )
        );
    }

    @Transactional(readOnly = true)
    public <T extends EntityProjection> List<T> findAllByFileIdAndParticipantId(
            Long participantId, Long fileId, Class<T> clazz
    ) {
        return CommonUtils.iterableToList(
                qrRepository.findAllByParticipantIdAndFileId(
                        participantId,
                        fileId,
                        clazz
                )
        );
    }

    public List<QRSimplestProjection> evictFromQrsCacheByFileId(Long participantId, Long fileId) {
        if (fileId == null || participantId == null) {
            return Collections.emptyList();
        }
        List<QRSimplestProjection> qrs = findAllByFileIdAndParticipantId(
                participantId, fileId,
                QRSimplestProjection.class
        );
        return evictFromCache(qrs);
    }

    public List<QRSimplestProjection> evictFromQrsCacheByFormId(Long participantId, Long formId) {
        if (formId == null || participantId == null) {
            return Collections.emptyList();
        }
        List<QRSimplestProjection> qrs = findAllByFormIdAndParticipantId(
                participantId, formId,
                QRSimplestProjection.class
        );
        return evictFromCache(qrs);
    }

    public List<QRSimplestProjection> evictFromCache(List<QRSimplestProjection> qrs) {
        Cache qrsCache = cacheManager.getCache(QR_CACHE_NAME);
        if (qrsCache == null) {
            return Collections.emptyList();
        }
        qrs.stream().map(QRSimplestProjection::getCode)
                .filter(Objects::nonNull)
                .forEach(c -> {
                    try {
                        qrsCache.evict(c);
                    } catch (Exception e) {
                        // cache value is not present
                    }
                });
        return qrs;
    }

    public void clearCache() {
        Cache qrsCache = cacheManager.getCache(QR_CACHE_NAME);
        if (qrsCache == null) {
            return;
        }
        qrsCache.clear();
    }
}
