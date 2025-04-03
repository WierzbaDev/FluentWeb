package org.wierzbadev.wordservice.exception.userWordExpection;

import lombok.Getter;

@Getter
public class UserWordException extends RuntimeException {
    private final UserWordError userWordError;

    public UserWordException(UserWordError error) {
        super(error.getMessage());
        this.userWordError = error;
    }
}
