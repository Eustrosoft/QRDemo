package org.eustrosoft.entitites.composite;

import lombok.Data;

import java.io.Serializable;

@Data
public class DictionaryCompositeId implements Serializable {
    private String name;
    private String code;
}
