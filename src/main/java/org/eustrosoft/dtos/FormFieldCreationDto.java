package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FormFieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldCreationDto {
    private String name;
    private String placeholder;
    private Integer fieldOrder;
    private FormFieldType fieldType;
    private Boolean isStatic;
    private Boolean isPublic;
}
