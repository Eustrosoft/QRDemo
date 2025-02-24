package org.eustrosoft.entitites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "qr", schema = "qrdemo")
public class QR extends DbEntity {
    public static final String TYPE = "QR";

    @Column(name = "code")
    private Long code;

    @Column(name = "data")
    private String data;

    @Column(name = "form_id")
    private Long formId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "form_id", insertable = false, updatable = false)
    private Form form;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "qr_file",
            joinColumns = {@JoinColumn(name = "qr_id")},
            inverseJoinColumns = {@JoinColumn(name = "file_id")}
    )
    private List<File> files;

    @Transient
    @JsonIgnore
    private QRRange range;
}
