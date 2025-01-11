package org.eustrosoft.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantBlockDto {
    private final Long id;
    private final String reason;
}
