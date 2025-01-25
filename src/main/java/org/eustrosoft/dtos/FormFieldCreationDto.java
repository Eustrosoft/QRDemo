package org.eustrosoft.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FormFieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormFieldCreationDto {
    private String name;
    private String caption;
    private String placeholder;
    private Integer fieldOrder;
    private FormFieldType fieldType;
    private Boolean isStatic;
    private Boolean isPublic;
}
