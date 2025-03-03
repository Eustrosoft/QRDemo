package org.eustrosoft.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.File;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormChangeDto extends EntityDto {
    private JsonNode data;
    private List<File> files;
    private List<@Valid FormFieldChangeDto> fields;
}
