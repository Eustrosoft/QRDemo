package org.eustrosoft.dtos;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.QRAction;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRDto extends EntityDto {
    private Long code;
    @JsonRawValue
    private String data;
    private QRAction action;
    private String redirect;
    private FormDto form;
    private List<FileDto> files;
}
