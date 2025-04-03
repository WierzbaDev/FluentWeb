package org.wierzbadev.scoreservice.service.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wierzbadev.scoreservice.exception.ScoreError;
import org.wierzbadev.scoreservice.exception.ScoreException;
import org.wierzbadev.scoreservice.model.UserScore;
import org.wierzbadev.scoreservice.model.dto.ranking.RankedUserProjection;
import org.wierzbadev.scoreservice.model.dto.user.UserScoreMessage;
import org.wierzbadev.scoreservice.model.dto.user.WordCerfLevel;
import org.wierzbadev.scoreservice.repository.UserScoreRepository;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
public class UserScoreService {
    private final UserScoreRepository userScoreRepository;

    public UserScoreService(UserScoreRepository userScoreRepository) {
        this.userScoreRepository = userScoreRepository;
    }

    public List<UserScore> readAllUsersScore() {
        log.info("Exposing all the score");
        return userScoreRepository.findAll();
    }

    public List<RankedUserProjection> readByTop20(long userId) {
        return userScoreRepository.findByTop20(userId);
    }

    public UserScore readUserScoreById(long id) {
       log.info("Read score by scoreId: {}", id);
        return userScoreRepository.findById(id)
                .orElseThrow(() -> new ScoreException(ScoreError.SCORE_NOT_FOUND));
    }

    public UserScore readUserScoreByUserId(long id) {
        log.info("Read score by user with id: {}", id);
        return userScoreRepository.findByUserId(id)
                .orElseThrow(() -> new ScoreException(ScoreError.SCORE_NOT_FOUND));
    }

    public UserScore createUserScore(UserScore userScore) {
        log.info("Created userScore for user with id: {}", userScore.getUserId());
        return userScoreRepository.save(userScore);
    }

    public UserScore patchUserScore(long id, UserScore userScore) {
        UserScore score = readUserScoreByUserId(id);

        log.info("Changed score for user with id: {}", id);
        score.setScore(userScore.getScore());
        return userScoreRepository.save(score);
    }

    public void mapMessageToScore(UserScoreMessage message) {
        if (message.isCorrect()) {
            UserScore score = userScoreRepository.findByUserId(message.getUserId())
                    .orElseGet(() -> {
                        UserScore newScore = new UserScore();
                        newScore.setUserId(message.getUserId());
                        newScore.setScore(BigInteger.ZERO);
                        return userScoreRepository.save(newScore);
                    });

            score.setScore(score.getScore().add(mapLevelToPoints(message.getWordCerfLevel())));
            userScoreRepository.save(score);
        }
    }

    public void deleteScore(long id) {
        UserScore toDelete = readUserScoreByUserId(id);
        log.info("Deleted UserScore with id: {}", id);
        userScoreRepository.delete(toDelete);
    }

    private BigInteger mapLevelToPoints(WordCerfLevel level) {
        return switch (level) {
            case A1 -> BigInteger.valueOf(5);
            case A2 -> BigInteger.valueOf(10);
            case B1 -> BigInteger.valueOf(20);
            case B2 -> BigInteger.valueOf(30);
            case C1 -> BigInteger.valueOf(50);
            case C2 -> BigInteger.valueOf(100);
        };
    }
}
