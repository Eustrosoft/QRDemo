package org.eustrosoft.entitites.composite;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@Data
public class FormFieldCompositeId implements Serializable {
    @Column(name = "form_id", insertable = false, updatable = false)
    private Long formId;
    @Column(name = "name", insertable = false, updatable = false)
    private String name;
}
