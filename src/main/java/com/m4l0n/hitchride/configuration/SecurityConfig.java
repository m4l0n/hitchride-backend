package com.m4l0n.hitchride.configuration;

// Programmer's Name: Ang Ru Xian
// Program Name: SecurityConfig.java
// Description: This is a class that configures the security of the application
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.advice.CustomAccessDeniedHandler;
import com.m4l0n.hitchride.configuration.auth.FirebaseFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final FirebaseFilter firebaseFilter;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/webjars/**",
            "/error"
    };

    public SecurityConfig(FirebaseFilter firebaseFilter) {
        this.firebaseFilter = firebaseFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .cors().and().csrf().disable()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(STATELESS)
                )
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling()
                    .accessDeniedHandler(new CustomAccessDeniedHandler()).and()
                .addFilterBefore(firebaseFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.oauth2ResourceServer().jwt();
        return httpSecurity.build();
    }

}
