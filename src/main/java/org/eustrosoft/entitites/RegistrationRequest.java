package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.entitites.enums.RegistrationStatus;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "registration_request", schema = "qrdemo")
public class RegistrationRequest extends DbEntity {

    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "referrer_url")
    private String referrerUrl;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "website")
    private String website;
    @Column(name = "organization")
    private String organization;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "country")
    private String country;
    @Column(name = "city")
    private String city;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private RegistrationStatus status;
    @Column(name = "status_msg")
    private String statusMsg;
    @Column(name = "registration_id")
    private UUID registrationId;
    @Lazy
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "reviewed_by", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Participant reviewedBy;
    @Column(name = "reviewed_at")
    private Date reviewedAt;
}
