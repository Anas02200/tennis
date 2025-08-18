package org.infrastructure.web.request;

public record GameSequenceDto(String sequence) {
    public GameSequenceDto {
        if (sequence == null || sequence.trim().isEmpty()) {
            throw new IllegalArgumentException("Sequence cannot be null or empty");
        }
    }
}