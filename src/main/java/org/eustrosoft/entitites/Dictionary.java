package org.eustrosoft.entitites;

import lombok.Data;
import org.eustrosoft.entitites.composite.DictionaryCompositeId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@IdClass(DictionaryCompositeId.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "dictionary", schema = "qrdemo")
public class Dictionary implements Serializable {

    @Id
    @Column(name = "name")
    private String name;

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "value")
    private String value;

    @Column(name = "description")
    private String description;
}
