package org.eustrosoft.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "QRDemo Swagger",
                description = "API системы QRDEMO",
                version = "1.0.0",
                contact = @Contact(
                        name = "Seleznev Pavel",
                        email = "yadzuka@eustrosoft.org"
                )
        )
)
public class OpenApiConfig {

}
