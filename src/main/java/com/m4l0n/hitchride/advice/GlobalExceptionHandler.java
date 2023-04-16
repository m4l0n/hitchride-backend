package com.m4l0n.hitchride.advice;

import com.m4l0n.hitchride.response.NegativeResponse;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public NegativeResponse handleInternalServerException(Throwable ex) {
        Throwable cause = Optional.ofNullable(ex.getCause()).orElse(ex);

        return handleDefault(cause);
    }

    private NegativeResponse handleDefault(Throwable ex) {
        log.error("Error while processing the request", ex);
        return ResponseAPI.negativeResponse(StatusCode.INTERNAL_SERVER_ERROR, defaultIfNull(ex.getMessage(),
                "Error while processing the request"), ex);
    }
}
