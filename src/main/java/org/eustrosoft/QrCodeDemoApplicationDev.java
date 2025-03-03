package org.eustrosoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class QrCodeDemoApplicationDev {

    public static void main(String[] args) {
        SpringApplication.run(QrCodeDemoApplicationDev.class);
    }

    @Bean
    @Profile("dev")
    CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://95.31.171.231:5173");
        allowedOrigins.add("http://192.168.3.2:5173");
        allowedOrigins.add("http://192.168.4.16:5173");
        allowedOrigins.add("http://192.168.4.17:5173");
        allowedOrigins.add("http://localhost:5173");

        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("PUT", "PATCH", "POST", "GET", "DELETE", "OPTIONS"));
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedHeaders(Arrays.asList("access-control-allow-headers", "access-control-allow-methods", "access-control-allow-origin", "content-type", "authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
