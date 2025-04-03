package org.wierzbadev.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorInfo> exceptionHandle(UserException e) {
        ErrorInfo errorInfo = new ErrorInfo(e.getMessage());
        return ResponseEntity.status(mapStatusCode(e.getUserError()))
                .body(errorInfo);
    }

    public HttpStatus mapStatusCode(UserError error) {
        return switch (error) {
            case USER_NOT_FOUND, USER_INVALID_TOKEN -> HttpStatus.NOT_FOUND;
            case USER_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case USER_IS_BANNED, USER_BLOCKED_TOKEN, USER_NOT_VERIFIED -> HttpStatus.FORBIDDEN;
            case USER_INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
            case USER_INVALID_PASSWORD_TOKEN, USER_PASSWORD_EXPIRED,
                 USER_PASSWORD_NOT_MATCH, USER_PASSWORD_IS_WEAK, USER_EMPTY_TOKEN -> HttpStatus.BAD_REQUEST;
        };
    }
}
