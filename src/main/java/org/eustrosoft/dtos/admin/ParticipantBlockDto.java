package org.eustrosoft.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ParticipantBlockDto {
    @NotNull
    private final Long id;
    private final String reason;
}
