package org.eustrosoft.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FormFieldType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormFieldCreationDto {
    @Size(max = 128)
    private String name;
    @Size(max = 256)
    private String caption;
    @Size(max = 1024)
    private String placeholder;
    private Integer fieldOrder;
    private FormFieldType fieldType;
    @NotNull
    private Boolean isStatic;
    @NotNull
    private Boolean isPublic;
}
