package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "gs_label", schema = "qrdemo")
public class GSLabel extends DbEntity {
    public static final String TYPE = "GSL";

    @Column(name = "qr_id")
    private Long qrId;

    @Column(name = "gtin")
    private Long gtin;

    @Column(name = "rtype")
    private String rType;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "tail")
    private String tail;

    @Column(name = "comment")
    private String comment;
}
