package org.eustrosoft.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String description;

    private List<QRRangeDto> ranges;
}
