package org.wierzbadev.scoreservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScoreExceptionHandler {

    @ExceptionHandler(ScoreException.class)
    public ResponseEntity<ErrorInfo> handleException(ScoreException e) {
        ErrorInfo errorInfo = new ErrorInfo(e.getMessage());
        return ResponseEntity.status(mapErrorCode(e.getScoreError())).body(errorInfo);
    }

    public HttpStatus mapErrorCode(ScoreError error) {
        return switch (error) {
            case SCORE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case SCORE_NOT_IN_RANKING -> HttpStatus.BAD_REQUEST;
            case SCORE_SERVICE_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
