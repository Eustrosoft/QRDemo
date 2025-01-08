package org.eustrosoft.repositories.projections;

import java.util.List;

public interface FormQrsProjection extends FormSimpleProjection {

    List<QRSimplestProjection> getQrs();
}
