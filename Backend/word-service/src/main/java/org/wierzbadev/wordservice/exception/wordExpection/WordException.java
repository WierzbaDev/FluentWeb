package org.wierzbadev.wordservice.exception.wordExpection;

import lombok.Getter;

@Getter
public class WordException extends RuntimeException {
    private final WordError wordError;

    public WordException(WordError wordError) {
        super(wordError.getMessage());
        this.wordError = wordError;
    }
}
