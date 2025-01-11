package org.eustrosoft.repositories.projections;

import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.entitites.Role;

import java.util.Collection;

public interface ParticipantAdminSimpleProjection extends EntityProjection {

    String getUsername();

    String getEmail();

    Boolean getActive();

    Boolean getBanned();

    Collection<Role> getRoles();

    Collection<QRRange> getRanges();
}
