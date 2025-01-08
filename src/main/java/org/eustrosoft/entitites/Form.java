package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.repositories.projections.FormSimpleProjection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "form", schema = "qrdemo")
@NamedEntityGraph(name="fieldsEntityGraph", attributeNodes={
        @NamedAttributeNode("fields")
})
public class Form extends DbEntity implements FormSimpleProjection {
    public static final String TYPE = "FM";

    @Column(name = "data")
    private String data;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "form_id", updatable = false)
    private List<FormField> fields;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "form_file",
            joinColumns = {@JoinColumn(name = "form_id")},
            inverseJoinColumns = {@JoinColumn(name = "file_id")}
    )
    private List<File> files;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = false)
    private List<QR> qrs;
}
