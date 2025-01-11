package org.eustrosoft.repositories.projections;

import java.util.Collection;

public interface ParticipantAdminProjection extends ParticipantAdminSimpleProjection {

    String getBannedReason();

    Collection<QRSimplestProjection> getQrs();
}
