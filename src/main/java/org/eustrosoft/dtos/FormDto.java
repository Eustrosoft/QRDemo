package org.eustrosoft.dtos;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDto extends EntityDto {
    @JsonRawValue
    private String data;
    private List<FormFieldDto> fields;
    private List<FileDto> files;
}
