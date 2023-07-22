package com.m4l0n.hitchride.response;

// Programmer's Name: Ang Ru Xian
// Program Name: NegativeResponse.java
// Description: A class that represents a negative response from the server
// Last Modified: 22 July 2023

public class NegativeResponse extends Response{

    public NegativeResponse(StatusCode statusCode, String message, Object result) {
        super(statusCode, message, result);
    }

}
