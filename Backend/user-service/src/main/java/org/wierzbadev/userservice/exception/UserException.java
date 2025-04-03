package org.wierzbadev.userservice.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final UserError userError;

    public UserException(UserError userError) {
        super(userError.getMessage());
        this.userError = userError;
    }
}
