package com.m4l0n.hitchride.response;

// Programmer's Name: Ang Ru Xian
// Program Name: StatusCode.java
// Description: An enum class that represents the status code of a response
// Last Modified: 22 July 2023

import lombok.Getter;

@Getter
public enum StatusCode {

    OK(0),
    UNAUTHORIZED(1),
    INTERNAL_SERVER_ERROR(2),
    NOT_FOUND(3);

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

}
