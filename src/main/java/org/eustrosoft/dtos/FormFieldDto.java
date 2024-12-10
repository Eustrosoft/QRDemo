package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FormFieldType;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldDto extends EntityDto {
    private String name;
    private String placeholder;
    private FormFieldType type;
    private Boolean isStatic;
    private Boolean isPublic;
}
