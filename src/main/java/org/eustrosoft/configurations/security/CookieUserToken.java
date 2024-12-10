package org.eustrosoft.configurations.security;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.utils.HttpUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
@Primary
@RequiredArgsConstructor
public class CookieUserToken implements UserToken {

    public static final String JWT_COOKIE_NAME = "JWT_COOKIE_TOKEN";

    private final HttpUtils httpUtils;

    @Override
    public String getToken() {
        Cookie[] cookies = httpUtils.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
