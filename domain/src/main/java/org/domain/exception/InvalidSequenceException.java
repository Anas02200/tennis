package org.domain.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidSequenceException extends RuntimeException {
    public InvalidSequenceException(String message) {
        super(message);
        log.info(message);
    }
}