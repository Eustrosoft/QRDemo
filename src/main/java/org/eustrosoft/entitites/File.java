package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FileStorageType;
import org.eustrosoft.repositories.projections.FileProjection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "file", schema = "qrdemo")
public class File extends DbEntity implements FileProjection {
    public static final String TYPE = "FILE";

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "extension")
    private String extension;

    @Column(name = "active")
    private Boolean isActive;

    @Column(name = "last_accessed")
    private Date lastAccessed;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "public")
    private Boolean isPublic;

    @Column(name = "storage_place")
    @Enumerated(EnumType.STRING)
    private FileStorageType storagePlace;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "file_size")
    private Long fileSize;

    public String getFileType() {
        if (fileType == null) {
            return "application/octet-stream";
        }
        return fileType;
    }

    public File(Long id) {
        super(id);
    }

    @PrePersist
    protected void prePersist() {
        setLastAccessed(new Date());
    }
}
