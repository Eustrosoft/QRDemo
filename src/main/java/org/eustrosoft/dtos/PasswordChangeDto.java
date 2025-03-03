package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDto {
    @Size(max = 128)
    private String oldPassword;
    @Size(max = 128)
    private String newPassword;
    @Size(max = 128)
    private String confirmNewPassword;
}
