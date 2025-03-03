package org.eustrosoft.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.entitites.QR;
import org.eustrosoft.repositories.projections.QRSimplestProjection;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParticipantDto extends UserDto {
    private String lei;
    private String address;
    private String organization;
    private String website;
    private String description;
    private Boolean banned;
    private String bannedReason;
    private Boolean active;

    private List<QRRangeDto> ranges;
    private List<QRDto> qrs;
}
