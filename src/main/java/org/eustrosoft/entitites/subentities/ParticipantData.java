package org.eustrosoft.entitites.subentities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.entitites.DbEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "participant", schema = "qrdemo")
public class ParticipantData extends DbEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "lei")
    private String lei;

    @Column(name = "address")
    private String address;

    @Column(name = "website")
    private String website;

    @Column(name = "organization")
    private String organization;
}
