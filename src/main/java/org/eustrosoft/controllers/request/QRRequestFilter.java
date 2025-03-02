package org.eustrosoft.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRRequestFilter {
    private Long rangeId;

    public boolean isEmptyFilters() {
        return rangeId == null;
    }
}
