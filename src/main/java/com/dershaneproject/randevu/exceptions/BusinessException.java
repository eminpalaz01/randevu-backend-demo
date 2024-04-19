package com.dershaneproject.randevu.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
public class BusinessException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final List<String> errorMessages;

    public BusinessException(HttpStatus httpStatus, List<String> errorMessages) {
        super();
        this.errorMessages = errorMessages;
        this.httpStatus = httpStatus;
    }

}
