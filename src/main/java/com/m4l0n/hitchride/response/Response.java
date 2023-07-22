package com.m4l0n.hitchride.response;

// Programmer's Name: Ang Ru Xian
// Program Name: Response.java
// Description: An abstract class that represents a response from the server
// Last Modified: 22 July 2023

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
