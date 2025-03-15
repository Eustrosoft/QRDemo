package org.eustrosoft.repositories.projections;

import org.eustrosoft.entitites.enums.QRAction;

import java.util.List;

public interface QRProjection extends QRSimplestProjection {

    QRAction getAction();

    String getRedirect();

    String getData();

    List<FileProjection> getFiles();

    FormComplexProjection getForm();
}
