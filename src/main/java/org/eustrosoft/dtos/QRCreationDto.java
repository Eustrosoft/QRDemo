package org.eustrosoft.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCreationDto {
    private String name;
    private String description;
    private JsonNode data;
    private List<Long> filesIds;
    private Long formId;

    // Range or code needed for creating, or will be used default range
    private Long code;
    private Long rangeId;
}
