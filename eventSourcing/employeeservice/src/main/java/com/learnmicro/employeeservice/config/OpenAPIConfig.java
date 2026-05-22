package com.learnmicro.employeeservice.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Employee Api Specification",
                description = "Api documentation for Employee Service",
                version = "1.0",
                contact = @Contact(
                        name = "Trung Do",
                        email = "dangtrungdoee2bk@gmail.com",
                        url = "https://trungdo.com"
                ),
                license = @License(
                        name = "MIT license",
                        url = "https://trungdo.com/license"
                ),
                termsOfService = "https://trungdo.com/terms"
        ),
        servers = {
                @Server(
                description = "Local ENV",
                url = "https://locolhost:9002"
                ),
                @Server(
                        description = "Dev ENV",
                        url = "https://employee-service.dev.com"
                ),
                @Server(
                        description = "Prod ENV",
                        url = "https://employee-service.prod.com"
                )
        }
)
public class OpenAPIConfig {
}
