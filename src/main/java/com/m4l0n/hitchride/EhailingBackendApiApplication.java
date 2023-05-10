package com.m4l0n.hitchride;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
@OpenAPIDefinition
public class EhailingBackendApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EhailingBackendApiApplication.class, args);
    }

    @Bean
    DispatcherServlet dispatcherServlet () {
        DispatcherServlet ds = new DispatcherServlet();
        ds.setThrowExceptionIfNoHandlerFound(true);
        return ds;
    }

}
