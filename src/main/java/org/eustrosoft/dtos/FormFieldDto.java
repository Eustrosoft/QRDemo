package org.eustrosoft.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FormFieldType;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormFieldDto extends EntityDto {
    private String placeholder;
    private Integer fieldOrder;
    private FormFieldType fieldType;
    private Boolean isStatic;
    private Boolean isPublic;
}
