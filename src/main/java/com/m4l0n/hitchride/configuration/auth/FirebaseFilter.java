package com.m4l0n.hitchride.configuration.auth;

// Programmer's Name: Ang Ru Xian
// Program Name: FirebaseFilter.java
// Description: This is a class that filters requests to the server, and sets the security context of the request
// Last Modified: 22 July 2023


import com.google.firebase.auth.FirebaseToken;
import com.m4l0n.hitchride.service.FirebaseService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirebaseFilter extends OncePerRequestFilter {

    private final FirebaseService firebaseService;

    public FirebaseFilter(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String idToken = request.getHeader("Authorization");
        if (StringUtils.hasText(idToken)) {
            try {
                FirebaseToken decodedToken = firebaseService.parseToken(idToken.substring(7));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(decodedToken.getUid(), decodedToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                throw new AuthenticationServiceException(e.getMessage());
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
