package org.eustrosoft.services.schedulers;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.services.caches.QRCacheControlService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.eustrosoft.configurations.QRCachingConfig.QR_CACHE_NAME;

@Component
@RequiredArgsConstructor
public class QRCacheCleaner {
    private final QRCacheControlService qrCacheControlService;

    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    public static final int REFRESH_RATE_SECONDS = 30;

    private static final long REFRESH_RATE = REFRESH_RATE_SECONDS * 1000;

    @Scheduled(fixedRate = REFRESH_RATE)
    public void clearCache() {
        try {
            qrCacheControlService.clearCache();
            LOGGER.log(Level.OFF, "Cache, named \"{0}\" was cleared", QR_CACHE_NAME);
        } catch (Exception ex) {
            LOGGER.log(Level.ALL, "Error while clearing cache: " + ex.getLocalizedMessage());
        }
    }
}
