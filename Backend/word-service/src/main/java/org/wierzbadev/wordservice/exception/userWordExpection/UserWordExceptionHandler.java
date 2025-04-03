package org.wierzbadev.wordservice.exception.userWordExpection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wierzbadev.wordservice.exception.ErrorInfo;

@RestControllerAdvice
public class UserWordExceptionHandler {

    @ExceptionHandler(UserWordException.class)
    public ResponseEntity<ErrorInfo> handleException(UserWordException e) {
        ErrorInfo errorInfo = new ErrorInfo(e.getMessage());
        return ResponseEntity.status(mapErrorCode(e.getUserWordError())).body(errorInfo);
    }

    public HttpStatus mapErrorCode(UserWordError error) {
        return switch (error) {
            case USER_WORD_NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }
}
