package org.wierzbadev.scoreservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScoreError {
    SCORE_NOT_FOUND("The score does not exists"),
    SCORE_NOT_IN_RANKING("The user is not in the ranking"),
    SCORE_SERVICE_ERROR("Error while communicating with User-Service");

    private final String message;
}
