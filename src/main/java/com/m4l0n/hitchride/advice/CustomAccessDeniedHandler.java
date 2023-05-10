package com.m4l0n.hitchride.advice;

import com.google.gson.Gson;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.response.StatusCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException {

        Response negResponse = ResponseAPI.negativeResponse(StatusCode.UNAUTHORIZED, "Access denied", null);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(403);
        response.getWriter().write(new Gson().toJson(negResponse));
        response.getWriter().flush();
    }
}