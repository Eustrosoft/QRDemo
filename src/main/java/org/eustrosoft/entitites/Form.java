package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.repositories.projections.FormSimpleProjection;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "form", schema = "public")
public class Form extends DbEntity implements FormSimpleProjection {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
    
    @Column(name = "data")
    private String data;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "participant_form",
            joinColumns = @JoinColumn(name = "form_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Participant participant;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "form_blocks",
            joinColumns = @JoinColumn(name = "form_id"),
            inverseJoinColumns = @JoinColumn(name = "form_block_id")
    )
    private List<FormBlock> blocks;
}
