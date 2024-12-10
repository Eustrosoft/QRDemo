package org.eustrosoft.configurations.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.utils.HttpUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeaderUserToken implements UserToken {

    private final HttpUtils httpUtils;

    @Override
    public String getToken() {
        String authorization = httpUtils.getAttributes().getRequest().getHeader("Authorization");
        if (StringUtils.isNotEmpty(authorization)) {
            if (authorization.startsWith("Bearer ")) {
                return authorization.substring(7);
            }
        }
        return null;
    }
}
