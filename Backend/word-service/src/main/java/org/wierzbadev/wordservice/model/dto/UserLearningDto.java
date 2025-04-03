package org.wierzbadev.wordservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLearningDto {
    private long userId;
    private int failedWords;
    private int correctWords;
    private int countWordsToday;
}
