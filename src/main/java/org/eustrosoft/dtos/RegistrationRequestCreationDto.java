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
    private String firstName;
    @NotBlank
    @Size(max = 128)
    private String lastName;
    @NotBlank
    @Size(max = 256)
    private String email;
    @NotBlank
    @Size(max = 32)
    private String phoneNumber;
    @Size(max = 2048)
    private String website;
    @NotBlank
    @Size(max = 2048)
    private String organization;
    @Size(max = 128)
    private String country;
    @Size(max = 128)
    private String city;

    public boolean hasEmptyFields() {
        return StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName)
                || StringUtils.isBlank(email) || StringUtils.isBlank(phoneNumber)
                || StringUtils.isBlank(organization);
    }
}
