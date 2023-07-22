package com.m4l0n.hitchride.response;

// Programmer's Name: Ang Ru Xian
// Program Name: ResponseAPI.java
// Description: A class that consists of static methods to create a response from the server
// Last Modified: 22 July 2023

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
