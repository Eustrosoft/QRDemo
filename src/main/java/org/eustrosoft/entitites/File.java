package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eustrosoft.entitites.enums.FileStorageType;
import org.eustrosoft.repositories.projections.FileProjection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "qr_file",
            joinColumns = {@JoinColumn(name = "file_id")},
            inverseJoinColumns = {@JoinColumn(name = "qr_id")}
    )
    private List<QR> qrs;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "form_file",
            joinColumns = {@JoinColumn(name = "file_id")},
            inverseJoinColumns = {@JoinColumn(name = "form_id")}
    )
    private List<Form> forms;

    public File(Long id) {
        super(id);
    }

    @PrePersist
    protected void prePersist() {
        setLastAccessed(new Date());
    }
}
