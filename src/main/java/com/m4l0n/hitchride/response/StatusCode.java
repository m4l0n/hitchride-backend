package com.m4l0n.hitchride.response;

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
