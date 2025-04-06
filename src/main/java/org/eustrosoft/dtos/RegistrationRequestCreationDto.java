package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestCreationDto extends EntityDto {
    @NotBlank
    @Size(max = 128)
    private String username;
    @NotBlank
    @Size(max = 256)
    private String password;
    @NotBlank
    @Size(max = 256)
    private String rePassword;
    @NotBlank
    @Size(max = 256)
    private String email;

    public boolean hasEmptyFields() {
        return StringUtils.isBlank(username) || StringUtils.isBlank(password)
                || StringUtils.isBlank(rePassword) || StringUtils.isBlank(email);
    }
}
