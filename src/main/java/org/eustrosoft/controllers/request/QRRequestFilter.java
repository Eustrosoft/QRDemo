package org.eustrosoft.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRRequestFilter {
    private ArrayList<Long> rangeId;

    public boolean isEmptyFilters() {
        return rangeId == null || rangeId.isEmpty();
    }
}
