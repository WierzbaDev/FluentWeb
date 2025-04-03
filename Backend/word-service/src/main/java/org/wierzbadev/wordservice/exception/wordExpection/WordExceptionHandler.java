package org.wierzbadev.wordservice.exception.wordExpection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wierzbadev.wordservice.exception.ErrorInfo;

@RestControllerAdvice
public class WordExceptionHandler {

    @ExceptionHandler(WordException.class)
    public ResponseEntity<ErrorInfo> handleException(WordException e) {
        ErrorInfo errorInfo = new ErrorInfo(e.getMessage());
        return ResponseEntity.status(mapErrorCode(e.getWordError()))
                .body(errorInfo);
    }

    public HttpStatus mapErrorCode(WordError error) {
        return switch (error) {
            case WORD_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case WORD_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case WORD_MISSING_REQUIRED_FIELDS -> HttpStatus.BAD_REQUEST;
        };
    }
}
