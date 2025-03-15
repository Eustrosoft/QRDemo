package org.eustrosoft.entitites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.entitites.enums.QRAction;
import org.eustrosoft.repositories.projections.FormWithFieldsProjection;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.context.annotation.Lazy;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "qr", schema = "qrdemo")
public class QR extends DbEntity implements QRSimpleProjection {
    public static final String TYPE = "QR";

    @Column(name = "code")
    private Long code;

    @Column(name = "data")
    private String data;

    @Column(name = "form_id")
    private Long formId;

    // TODO: entity uses both @NotFound(action = NotFoundAction.IGNORE) and FetchType.LAZY.
    //  The NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "form_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy
    private Form form;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "qr_file",
            joinColumns = {@JoinColumn(name = "qr_id")},
            inverseJoinColumns = {@JoinColumn(name = "file_id")}
    )
    @Lazy
    private List<File> files;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "action")
    private QRAction action;

    @Column(name = "redirect")
    private String redirect;

    @Transient
    @JsonIgnore
    @Lazy
    private QRRange range;

    @Transient
    @JsonIgnore
    @Lazy
    private FormWithFieldsProjection formWithFieldsProjection;

    public static final class SortAttributeNames {
        public static final String ATTR_CREATED = "created";
    }
}
