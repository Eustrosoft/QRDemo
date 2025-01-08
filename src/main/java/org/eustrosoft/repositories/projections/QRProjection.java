package org.eustrosoft.repositories.projections;

import java.util.List;

public interface QRProjection extends QRSimplestProjection {

    String getData();

    List<FileProjection> getFiles();

    FormComplexProjection getForm();
}
