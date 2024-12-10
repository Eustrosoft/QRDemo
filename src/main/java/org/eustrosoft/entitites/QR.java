package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "qr", schema = "public")
public class QR extends DbEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "code")
    private Long code;

    @Column(name = "data")
    private String data;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "participant_qr",
            joinColumns = @JoinColumn(name = "qr_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Participant participant;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "form_qr",
            joinColumns = @JoinColumn(name = "qr_id"),
            inverseJoinColumns = @JoinColumn(name = "form_id")
    )
    private Form form;

}
