package com.joele.inventory.infrastructure.web;

import com.joele.inventory.application.usecase.inventory.ProductNotFoundException;
import com.joele.inventory.common.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail notFound(ProductNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({DomainException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    ProblemDetail badRequest(Exception exception) {
        String message = exception.getMessage() == null ? "Invalid request" : exception.getMessage();
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    }
}
