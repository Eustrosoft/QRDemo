package org.eustrosoft.entitites.composite;

import lombok.Data;

import java.io.Serializable;

@Data
public class DbEntityEutrosoftCompositeId implements Serializable {
    private Long ZOID;
    private Long ZRID;
    private Long ZVER;
}
