package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormBlockCreationDto {
    private String name;
    private String description;
    private List<FormFieldCreationDto> fields;
}
