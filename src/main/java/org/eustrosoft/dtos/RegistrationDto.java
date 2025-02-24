package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.QRRange;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String lei;
    private String organization;
    private String address;
    private String website;
    private List<RoleDto> roles;
    private List<QRRange> ranges;

    public RegistrationDto(String username, String password, String confirmPassword, String email) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
    }
}
