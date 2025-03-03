package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.QRRange;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto extends ParticipantDto {
    @Size(max = 128)
    private String username;
    @Size(max = 128)
    private String password;
    @Size(max = 128)
    private String confirmPassword;
    @Email
    private String email;
    @Size(max = 256)
    private String lei;
    @Size(max = 256)
    private String organization;
    @Size(max = 256)
    private String address;
    @Size(max = 256)
    private String website;

    public RegistrationDto(String username, String password, String confirmPassword, String email) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
    }
}
