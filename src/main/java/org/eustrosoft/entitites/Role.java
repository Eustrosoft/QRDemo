package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "role", schema = "qrdemo")
public class Role extends DbEntity {
    public static final String TYPE = "RL";

    @Column(name = "active")
    private Boolean active;
}
