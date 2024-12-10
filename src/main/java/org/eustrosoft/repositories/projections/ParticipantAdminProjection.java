package org.eustrosoft.repositories.projections;

import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.entitites.Role;

import java.util.Collection;

public interface ParticipantAdminProjection {

    Long getId();

    String getUsername();

    String getEmail();

    Boolean getActive();

    Boolean getBanned();

    String getBannedReason();

    Collection<Role> getRoles();

    Collection<QRSimplestProjection> getQrs();

    Collection<QRRange> getRanges();
}
