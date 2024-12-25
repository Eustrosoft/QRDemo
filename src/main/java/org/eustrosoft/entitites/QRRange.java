package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "qr_range", schema = "qrdemo")
public class QRRange extends DbEntity {
    public static final String TYPE = "QRR";

    @Column(name = "from_range")
    private Long from;

    @Column(name = "to_range")
    private Long to;
}
