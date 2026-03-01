package org.eustrosoft.entitites.composite;

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Data
public class PCodeCompositeId implements Serializable {
    private Long docId;
    @Generated(GenerationTime.INSERT)
    private Long rowId;
}
