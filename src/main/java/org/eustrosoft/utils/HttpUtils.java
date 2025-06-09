package org.eustrosoft.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public final class HttpUtils {
    private final JwtTokenUtils jwtTokenUtils;

    public void setCookie(String key, String value, boolean httpOnly, boolean secured) {
        setCookie(key, value, httpOnly, secured, jwtTokenUtils.getLifetime().getSeconds());
    }

    public void setCookie(String key, String value, boolean httpOnly, boolean secured, long maxAge) {
        if (StringUtils.isEmpty(key) || Objects.isNull(value)) {
            throw new IllegalArgumentException("key or values is empty");
        }

        ServletRequestAttributes attributes = getAttributes();
        HttpServletResponse response = attributes.getResponse();

        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(key, value);
        cookieBuilder.path("/");
        cookieBuilder.secure(secured);
        cookieBuilder.httpOnly(httpOnly);
        cookieBuilder.maxAge(maxAge);
        cookieBuilder.sameSite("Lax");
        ResponseCookie cookie = cookieBuilder.build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static String getASCIIUrl(final String url) throws MalformedURLException {
        if (StringUtils.isBlank(url)) {
            return "";
        }
        URL urlObj = new URL(url);
        String protocol = urlObj.getProtocol();
        String idn = IDN.toASCII(urlObj.getHost());
        String finalPath = String.format("%s://%s", protocol, idn);
        if (urlObj.getPath() != null) {
            try {
                finalPath = finalPath.concat(IDN.toASCII(urlObj.getPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (urlObj.getQuery() != null) {
            try {
                finalPath = finalPath.concat(IDN.toASCII(urlObj.getQuery()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return finalPath;
    }

    public Cookie[] getCookies() {
        return getAttributes().getRequest().getCookies();
    }

    public ServletRequestAttributes getAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }
}
