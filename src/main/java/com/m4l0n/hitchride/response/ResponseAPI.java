package com.m4l0n.hitchride.response;

import java.util.Collections;

public class ResponseAPI {

    public static Response positiveResponse(Object result) {
        return new PositiveResponse(result);
    }

    public static Response emptyPositiveResponse() {
        return new PositiveResponse(Collections.emptyList());
    }

    public static NegativeResponse negativeResponse(StatusCode statusCode, String message,Object result) {
        return new NegativeResponse(statusCode, message, result);
    }

}
