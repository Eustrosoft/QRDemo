package org.eustrosoft.entitites;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@MappedSuperclass
public class DbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created", insertable = false, updatable = false)
    private Date created;

    @Column(name = "updated", insertable = false)
    private Date updated;

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
}
