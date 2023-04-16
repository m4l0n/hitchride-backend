package com.m4l0n.hitchride.advice;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException {

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("timestamp", new JsonPrimitive(LocalDate.now().toString()));
        jsonObject.add("status", new JsonPrimitive(403));
        jsonObject.add("message", new JsonPrimitive("Access denied"));

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(403);
        response.getWriter().write(jsonObject.toString());
        response.getWriter().flush();
    }
}