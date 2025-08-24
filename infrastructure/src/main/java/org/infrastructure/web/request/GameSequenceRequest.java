package org.infrastructure.web.request;

public record GameSequenceRequest(String sequence) {
    public GameSequenceRequest {
        if (sequence == null || sequence.trim().isEmpty()) {
            throw new IllegalArgumentException("Sequence cannot be null or empty");
        }
    }
}