package org.eustrosoft.repositories.projections;

public interface EntityProjection extends SimpleProjection {

    Long getParticipantId();

    String getName();

    String getDescription();

    String getType();
}
