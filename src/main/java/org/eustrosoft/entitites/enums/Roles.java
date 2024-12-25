package org.eustrosoft.entitites.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Roles {
    USER("ROLE_USER"),
    SALESMAN("ROLE_SALESMAN"),
    ADMIN("ROLE_ADMIN");

    @Getter
    final String name;
}
