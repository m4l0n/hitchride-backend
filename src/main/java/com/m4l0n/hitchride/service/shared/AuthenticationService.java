package com.m4l0n.hitchride.service.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getAuthenticatedUsername() {
        Authentication authentication = getAuthentication();
        log.info("getAuthenticatedUsername: {}", authentication.getName());
        return authentication.getName();
    }

}
