package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FileStorageType;
import org.eustrosoft.repositories.projections.FileProjection;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@IdClass(DbEntityEustrosoft.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "file_blob", schema = "qrdemo")
public class FileBlob extends DbEntityEustrosoft {
    public static final String TYPE = "FILE_BLOB";

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "chunk")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] chunk;

    @Column(name = "no")
    private Long no;

    @Column(name = "size")
    private Long size;

    @Column(name = "crc32")
    private Long crc32;
}
