package org.eustrosoft.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormCreationDto {
    private String name;
    private String description;
    private JsonNode data;
    private List<FormBlockCreationDto> blocks;
}
