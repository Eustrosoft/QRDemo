package org.eustrosoft.repositories.projections;

import java.util.List;

public interface FileComplexProjection extends FileProjection {

    List<EntityProjection> getQrs();

    List<EntityProjection> getForms();
}
