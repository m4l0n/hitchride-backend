package com.m4l0n.hitchride.configuration;

import com.m4l0n.hitchride.advice.CustomAccessDeniedHandler;
import com.m4l0n.hitchride.configuration.auth.FirebaseFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final FirebaseFilter firebaseFilter;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/webjars/**"
    };

    public SecurityConfig(FirebaseFilter firebaseFilter) {
        this.firebaseFilter = firebaseFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .addFilterBefore(firebaseFilter, BearerTokenAuthenticationFilter.class)
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
                    .accessDeniedHandler(new CustomAccessDeniedHandler());
        httpSecurity.oauth2ResourceServer().jwt();
        return httpSecurity.build();
    }

}
