package org.eustrosoft.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableScheduling
@EnableWebSecurity
@EnableJpaRepositories(value = {"org.eustrosoft"})
public class CommonConfigurations {
}
