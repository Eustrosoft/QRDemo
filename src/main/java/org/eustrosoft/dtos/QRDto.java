package org.eustrosoft.dtos;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRDto extends EntityDto {
    private String name;
    private String description;
    private Long code;
    @JsonRawValue
    private String data;
    private FormDto form;
}
