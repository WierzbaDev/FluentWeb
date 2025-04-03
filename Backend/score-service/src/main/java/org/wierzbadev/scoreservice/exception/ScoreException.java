package org.wierzbadev.scoreservice.exception;

import lombok.Getter;

@Getter
public class ScoreException extends RuntimeException {
    private final ScoreError scoreError;

    public ScoreException(ScoreError scoreError, String message) {
        super(message);
        this.scoreError = scoreError;
    }

    public ScoreException(ScoreError scoreError) {
        super(scoreError.getMessage());
        this.scoreError = scoreError;
    }
}
