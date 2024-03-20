package com.dershaneproject.randevu.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
public class BusinessException extends RuntimeException{

    private final HttpStatus httpStatus;

    public BusinessException(HttpStatus httpStatus, List<String> errorMessages) {
        super(errorMessages.toString());
        this.httpStatus = httpStatus;
    }

}
