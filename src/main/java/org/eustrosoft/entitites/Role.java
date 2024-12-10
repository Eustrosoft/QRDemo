package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "role", schema = "public")
public class Role extends DbEntity {

    @Column(name = "name")
    private String name;

    @Getter
    @RequiredArgsConstructor
    public enum Names {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");

        final String name;
    }
}
