package org.eustrosoft.configurations;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Getter
@Configuration
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class QRDemoPropertiesReadPostProcessor implements EnvironmentPostProcessor {
    public static final String SETTINGS_KEY = "key";
    public static final String SETTINGS_VALUE = "value";
    private static final String PROPERTIES_DB_SOURCE_NAME = "qrDemoDatabaseProperties";
    private static final String PROPERTIES_FILE_SOURCE_NAME = "qrDemoFileProperties";
    private static final String PROPERTY_NAME_DB_CONFIG_LOCATION = "dbFileLocation";
    private static final String PROPERTY_NAME_DB_USERNAME = "spring.datasource.username";
    private static final String PROPERTY_NAME_DB_PASSWORD = "spring.datasource.password";
    private static final String PROPERTY_NAME_DB_URL = "spring.datasource.url";
    private static final String PROPERTY_NAME_DB_DRIVER = "spring.datasource.driver-class-name";
    private static final String SQL_SELECT_SETTINGS = "SELECT * FROM qrdemo.settings";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> propertySource = new HashMap<>();

        try {
            readDbProperties(environment);

            DataSource ds = DataSourceBuilder
                    .create()
                    .username(environment.getProperty(PROPERTY_NAME_DB_USERNAME))
                    .password(environment.getProperty(PROPERTY_NAME_DB_PASSWORD))
                    .url(environment.getProperty(PROPERTY_NAME_DB_URL))
                    .driverClassName(environment.getProperty(PROPERTY_NAME_DB_DRIVER))
                    .build();

            Connection connection = ds.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_SETTINGS);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                propertySource.put(rs.getString(SETTINGS_KEY), rs.getString(SETTINGS_VALUE));
            }
            rs.close();
            preparedStatement.clearParameters();
            preparedStatement.close();
            connection.close();

            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTIES_DB_SOURCE_NAME, propertySource));
            System.out.println("Loaded properties from database");
        } catch (Throwable e) {
            System.err.println("Failed to load properties from database: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    private void readDbProperties(ConfigurableEnvironment environment) throws Exception {
        String dbPropertiesFilePath = environment.getProperty(PROPERTY_NAME_DB_CONFIG_LOCATION);

        if (dbPropertiesFilePath != null && !dbPropertiesFilePath.isEmpty()) {
            Resource resource = new FileSystemResource(dbPropertiesFilePath);

            if (resource.exists()) {
                Properties properties = new Properties();
                properties.load(resource.getInputStream());

                PropertiesPropertySource propertySource = new PropertiesPropertySource(
                        PROPERTIES_FILE_SOURCE_NAME, properties
                );
                environment.getPropertySources().addLast(propertySource);

                System.out.println("Loaded properties from " + dbPropertiesFilePath);
            } else {
                System.err.println("Property file does not exist at path: " + dbPropertiesFilePath);
            }
        } else {
            System.err.println("No dbPropertiesFile path is defined in application.yml.");
            throw new FileNotFoundException("Properties file not found");
        }
    }
}
