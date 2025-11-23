package org.eustrosoft.entitites.composite;

import lombok.Data;

import java.io.Serializable;

@Data
public class PCodeCompositeId implements Serializable {
    private Long docId;
    private Long rowId;
}
