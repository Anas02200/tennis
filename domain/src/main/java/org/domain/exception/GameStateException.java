package org.domain.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameStateException extends RuntimeException{
    public GameStateException(String message) {
        super(message);
        log.info(message);
    }
}
