package com.m4l0n.hitchride;

// Programmer's Name: Ang Ru Xian
// Program Name: EhailingBackendApiApplication.java
// Description: Main class of the application, entry point of the application
// Last Modified: 22 July 2023

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
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

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("HRAsync-");
        executor.initialize();
        return executor;
    }

}
