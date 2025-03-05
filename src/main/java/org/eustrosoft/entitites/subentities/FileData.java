package org.eustrosoft.entitites.subentities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.entitites.DbEntity;
import org.eustrosoft.entitites.enums.FileStorageType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "file", schema = "qrdemo")
public class FileData extends DbEntity {

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "active")
    private Boolean isActive;

    @Column(name = "public")
    private Boolean isPublic;
}
