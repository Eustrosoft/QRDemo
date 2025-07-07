package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.QRAction;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRDto extends EntityDto {
    private Long code;
    private Map<String, Object> data;
    private QRAction action;
    private String redirect;
    private FormDto form;
    private List<FileDto> files;
}
