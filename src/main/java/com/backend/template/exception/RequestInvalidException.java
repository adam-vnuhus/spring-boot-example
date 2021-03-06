package com.backend.template.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class RequestInvalidException extends RuntimeException {
    public RequestInvalidException(String exception) {
        super(exception);
    }
}