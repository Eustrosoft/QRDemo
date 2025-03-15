package org.eustrosoft.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.QRAction;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCreationDto extends EntityDto {
    private JsonNode data;
    private List<Long> filesIds;
    private Long formId;
    private QRAction action;
    private String redirect;

    // Range or code needed for creating, or will be used default range
    private Long code;
    private Long rangeId;
}
