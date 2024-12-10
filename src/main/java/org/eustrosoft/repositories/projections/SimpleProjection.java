package org.eustrosoft.repositories.projections;

import java.util.Date;

public interface SimpleProjection {

    Long getId();

    Date getCreated();

    Date getUpdated();
}
