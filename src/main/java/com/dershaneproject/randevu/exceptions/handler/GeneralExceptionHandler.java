package com.dershaneproject.randevu.exceptions.handler;

import com.dershaneproject.randevu.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException exception)  {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
       List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
//                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusiness(BusinessException exception)  {
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getErrorMessages());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception exception)  {
        // "Genel Sunucu Hatasi" is will be message in next times and the current exc message will be logged
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(exception.getMessage() != null ? exception.getMessage() : "Genel Sunucu Hatasi"));
    }
}
