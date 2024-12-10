package org.eustrosoft.configurations.security;

import org.springframework.stereotype.Component;

@Component
public interface UserToken {

    String getToken();
}
