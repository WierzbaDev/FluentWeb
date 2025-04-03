package org.wierzbadev.notificationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserLearningDto {
    private long userId;
    private int failedWords;
    private int correctWords;
    private int countWordsToday;
}
