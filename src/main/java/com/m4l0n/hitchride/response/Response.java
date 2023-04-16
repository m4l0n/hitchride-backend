package com.m4l0n.hitchride.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Response {

    private final StatusCode statusCode;
    private final String message;
    private final Object result;

    public Response(Object result) {
        this.statusCode = StatusCode.OK;
        this.message = "";
        this.result = result;
    }

}
