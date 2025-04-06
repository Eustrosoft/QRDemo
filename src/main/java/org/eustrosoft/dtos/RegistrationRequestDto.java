package org.eustrosoft.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.RegistrationStatus;

import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto extends EntityDto {
    private String username;
    private String password;
    private String email;
    private String ipAddress;
    private String userAgent;
    private String referrerUrl;
    private String firstName;
    private String lastName;
    private String webSite;
    private String organization;
    private String phoneNumber;
    private String country;
    private String city;
    private RegistrationStatus status;
    private String statusMsg;
    private UUID registrationId;
    private ParticipantDto reviewedBy;
    private Date reviewedAt;
}
