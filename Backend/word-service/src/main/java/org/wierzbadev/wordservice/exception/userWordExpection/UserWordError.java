package org.wierzbadev.wordservice.exception.userWordExpection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserWordError {
    USER_WORD_NOT_FOUND("The user word does not exists");
    private final String message;
}
