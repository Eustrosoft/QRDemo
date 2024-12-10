package org.eustrosoft.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParticipantDto extends UserDto {
    private List<QRRangeDto> ranges;
}
