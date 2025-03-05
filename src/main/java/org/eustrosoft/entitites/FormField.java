package org.eustrosoft.entitites;

import lombok.Data;
import org.eustrosoft.entitites.enums.FormFieldType;

import javax.annotation.PreDestroy;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "form_field", schema = "qrdemo")
public class FormField implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "form_id")
    private Long formId;

    @Transient
    @MapsId("form_id")
    private Form form;

    @Column(name = "caption")
    private String caption;

    @Column(name = "participant_id")
    private Long participantId;

    @Column(name = "field_order")
    private Integer fieldOrder;

    @Column(name = "placeholder")
    private String placeholder;

    @Column(name = "field_type")
    private FormFieldType fieldType;

    @Column(name = "static")
    private Boolean isStatic;

    @Column(name = "public")
    private Boolean isPublic;

    @PrePersist
    protected void onPersist() {
        try {
            if (form != null) {
                setFormId(form.getId());
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("TYPE field is not defined in class " + getClass().getName());
        }
    }

    @PreUpdate
    protected void onUpdate() {
        try {
            if (form != null) {
                setFormId(form.getId());
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("TYPE field is not defined in class " + getClass().getName());
        }
    }

    @PreDestroy
    protected void onDestroy() {
        try {
            if (form != null) {
                setFormId(form.getId());
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("TYPE field is not defined in class " + getClass().getName());
        }
    }

    public FormField clone() {
        FormField clone = new FormField();
        clone.setId(this.getId());
        clone.setName(this.getName());
        clone.setCaption(this.getCaption());
        clone.setFormId(this.getFormId());
        clone.setParticipantId(this.getParticipantId());
        clone.setFieldOrder(this.getFieldOrder());
        clone.setPlaceholder(this.getPlaceholder());
        clone.setFieldType(this.getFieldType());
        clone.setIsStatic(this.getIsStatic());
        clone.setIsPublic(this.getIsPublic());
        return clone;
    }
}
