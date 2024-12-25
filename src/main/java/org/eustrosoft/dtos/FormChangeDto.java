package org.eustrosoft.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.File;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormChangeDto {
    private Long id;
    private String name;
    private String description;
    private JsonNode data;
    private List<FormFieldChangeDto> fields;
    private List<File> files;
}
