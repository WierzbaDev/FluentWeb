package org.wierzbadev.wordservice.exception.wordExpection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WordError {
    WORD_NOT_FOUND("The word does not exists!"),
    WORD_ALREADY_EXISTS("This word already exists!"),
    WORD_MISSING_REQUIRED_FIELDS("Method PUT requires a full object!");

    private final String message;
}
