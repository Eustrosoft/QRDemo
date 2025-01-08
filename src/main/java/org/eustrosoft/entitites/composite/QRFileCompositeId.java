package org.eustrosoft.entitites.composite;

import lombok.Data;

import java.io.Serializable;

@Data
public class QRFileCompositeId implements Serializable {
    private String qrId;
    private String fileId;
}
