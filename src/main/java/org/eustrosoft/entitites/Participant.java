package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.repositories.projections.ParticipantSettingsProjection;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "participant", schema = "qrdemo")
public class Participant extends DbEntity implements ParticipantSettingsProjection {
    public static final String TYPE = "PT";

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "referer")
    private Long referer;

    @Column(name = "lei")
    private String lei;

    @Column(name = "address")
    private String address;

    @Column(name = "website")
    private String website;

    @Column(name = "organization")
    private String organization;

    @Column(name = "settings")
    private String settings;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "banned")
    private Boolean banned;

    @Column(name = "banned_reason")
    private String bannedReason;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_id")
    private List<File> files;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_id")
    private List<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_id")
    private List<QRRange> ranges;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_id")
    private List<QR> qrs;
}

