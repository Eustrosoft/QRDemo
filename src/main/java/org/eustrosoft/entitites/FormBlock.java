package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "form_block", schema = "public")
public class FormBlock extends DbEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "form_block_fields",
            joinColumns = @JoinColumn(name = "form_block_id"),
            inverseJoinColumns = @JoinColumn(name = "form_field_id")
    )
    private List<FormField> fields;

}
