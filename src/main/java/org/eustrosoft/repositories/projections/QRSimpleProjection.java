package org.eustrosoft.repositories.projections;

public interface QRSimpleProjection extends SimpleProjection {

    String getName();

    String getDescription();

    Long getCode();

    FormSimpleProjection getForm();
}
