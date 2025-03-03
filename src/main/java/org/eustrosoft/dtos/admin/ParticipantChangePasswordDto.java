package org.eustrosoft.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ParticipantChangePasswordDto {
    @Size(max = 128)
    private final String password;
    @Size(max = 128)
    private final String confirmPassword;
}
