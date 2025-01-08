package org.eustrosoft.entitites.relations;

import org.eustrosoft.entitites.composite.QRFileCompositeId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "qr_file", schema = "qrdemo")
@IdClass(QRFileCompositeId.class)
public class QRFile {

    @Id
    @Column(name = "qr_id")
    private Long qrId;

    @Id
    @Column(name = "file_id")
    private Long fileId;

}
