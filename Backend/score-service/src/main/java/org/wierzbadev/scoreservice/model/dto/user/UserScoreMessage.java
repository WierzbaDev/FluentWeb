package org.wierzbadev.scoreservice.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserScoreMessage implements Serializable {
    private long userId;
    private long wordId;
    private boolean isCorrect;
    private WordCerfLevel wordCerfLevel;
}
