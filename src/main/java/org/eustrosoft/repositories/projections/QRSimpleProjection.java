package org.eustrosoft.repositories.projections;

public interface QRSimpleProjection extends QRSimplestProjection {

    FormWithFieldsProjection getForm();

    String getData();
}
