package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eustrosoft.entitites.enums.FormFieldType;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "form_field", schema = "public")
public class FormField extends DbEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "placeholder")
    private String placeholder;

    @Column(name = "type")
    private FormFieldType type;

    @Column(name = "static")
    private Boolean isStatic;

    @Column(name = "public")
    private Boolean isPublic;

    public FormField clone() {
        FormField clone = new FormField();
        clone.setName(this.getName());
        clone.setPlaceholder(this.getPlaceholder());
        clone.setType(this.getType());
        clone.setIsStatic(this.getIsStatic());
        clone.setIsPublic(this.getIsPublic());
        return clone;
    }
}
