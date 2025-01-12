package org.eustrosoft.configurations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class QRRangeConfig {

    private final Long rangeStart;
    private final Long rangeEnd;
    private final Integer codesForRange;

    @Autowired
    public QRRangeConfig(
            @Value("${ranges.rangeStart}") String rangeStart,
            @Value("${ranges.rangeEnd}") String rangeEnd,
            @Value("${ranges.codesForRange}") String codesForRange
    ) {
        this.rangeStart = Long.parseLong(rangeStart, 16);
        this.rangeEnd = Long.parseLong(rangeEnd, 16);
        this.codesForRange = Integer.parseInt(codesForRange);
    }
}
