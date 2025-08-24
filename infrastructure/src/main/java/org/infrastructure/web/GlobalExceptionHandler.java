package org.infrastructure.web;

import org.domain.exception.GameStateException;
import org.domain.exception.InvalidSequenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidSequenceException.class, GameStateException.class})
    public ProblemDetail handleInvalidSequenceException(InvalidSequenceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Sequence");
        return problemDetail;
    }
}