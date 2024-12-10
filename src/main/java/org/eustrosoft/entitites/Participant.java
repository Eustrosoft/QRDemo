package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.repositories.projections.ParticipantSettingsProjection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "participant", schema = "public")
@Entity
public class Participant extends DbEntity implements ParticipantSettingsProjection {

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "settings")
    private String settings;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "banned")
    private Boolean banned;

    @Column(name = "banned_reason")
    private String bannedReason;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "participant_roles",
            joinColumns = @JoinColumn(name = "participant_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "participant_qr",
            joinColumns = @JoinColumn(name = "participant_id"),
            inverseJoinColumns = @JoinColumn(name = "qr_id")
    )
    private Collection<QR> qrs;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "participant_qr_range",
            joinColumns = @JoinColumn(name = "participant_id"),
            inverseJoinColumns = @JoinColumn(name = "qr_range_id")
    )
    private Collection<QRRange> ranges;
}

