package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "qr_range", schema = "public")
public class QRRange extends DbEntity {

    @Column(name = "from_range")
    private Long from;

    @Column(name = "to_range")
    private Long to;
}
