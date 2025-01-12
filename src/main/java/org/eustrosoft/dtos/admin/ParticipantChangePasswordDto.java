package org.eustrosoft.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantChangePasswordDto {
    private final String password;
    private final String confirmPassword;
}
