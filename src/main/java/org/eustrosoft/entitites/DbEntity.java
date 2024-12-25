package org.eustrosoft.entitites;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.util.Date;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class DbEntity {
    public static final String FIELD_NAME_TYPE = "TYPE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", updatable = false)
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "participant_id", updatable = false)
    private Long participantId;

    @Column(name = "created", insertable = false, updatable = false)
    private Date created;

    @Column(name = "updated", insertable = false)
    private Date updated;

    protected DbEntity(Long id) {
        this.id = id;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

    @PrePersist
    protected void onPersist() {
        try {
            Field field = this.getClass().getDeclaredField(FIELD_NAME_TYPE);
            field.setAccessible(true);
            this.type = (String) field.get(this);
        } catch (Exception ex) {
            throw new IllegalArgumentException("TYPE field is not defined in class " + getClass().getName());
        }
    }
}
