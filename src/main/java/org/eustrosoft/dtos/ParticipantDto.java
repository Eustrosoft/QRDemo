package org.eustrosoft.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParticipantDto extends UserDto {
    private String lei;
    private String address;
    private String organization;
    private String website;
    private String description;

    private List<QRRangeDto> ranges;
}
