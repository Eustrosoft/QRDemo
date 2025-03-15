package org.eustrosoft.repositories.projections;

import org.eustrosoft.entitites.enums.QRAction;

public interface QRSimpleProjection extends QRSimplestProjection {

    QRAction getAction();

    String getRedirect();

    FormWithFieldsProjection getFormWithFieldsProjection();

    String getData();
}
