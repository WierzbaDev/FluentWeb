package org.wierzbadev.scoreservice.integration.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.wierzbadev.scoreservice.exception.ScoreError;
import org.wierzbadev.scoreservice.exception.ScoreException;
import org.wierzbadev.scoreservice.integration.BaseIT;
import org.wierzbadev.scoreservice.model.UserScore;
import org.wierzbadev.scoreservice.model.dto.user.UserScoreMessage;
import org.wierzbadev.scoreservice.model.dto.user.WordCerfLevel;
import org.wierzbadev.scoreservice.repository.UserScoreRepository;
import org.wierzbadev.scoreservice.service.logic.UserScoreService;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserScoreServiceIntegrationTest extends BaseIT {

    @Autowired
    private UserScoreService userScoreService;

    @Autowired
    private UserScoreRepository userScoreRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Should return all UserScore")
    void readAllUserScoreTest() {
        userScoreRepository.deleteAll();
        UserScore score = UserScore.builder()
                .score(BigInteger.ZERO)
                .userId(1)
                .build();

        userScoreService.createUserScore(score);

        List<UserScore> toTest = userScoreService.readAllUsersScore();

        assertFalse(toTest.isEmpty());
        assertEquals(1, toTest.size());
    }

    @Test
    @DisplayName("Throws ScoreException(SCORE_NOT_FOUND) when score does not exists")
    void readUserById_shouldThrows_ScoreException() {

        Exception exception = assertThrows(ScoreException.class, () ->
                userScoreService.readUserScoreById(Long.MAX_VALUE));
        assertEquals(ScoreError.SCORE_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Should return UserScore by id when user exists")
    void readUserById() {
        UserScore score = UserScore.builder()
                .score(BigInteger.ZERO)
                .userId(2)
                .build();

        UserScore user = userScoreService.createUserScore(score);

        var toTest = userScoreService.readUserScoreById(user.getId());

        assertEquals(score.getScore(), toTest.getScore());
        assertEquals(score.getUserId(), toTest.getUserId());
    }

    @Test
    @DisplayName("Throws ScoreException(SCORE_NOT_FOUND) when use method readUserByUserId and score does not exists")
    void readUserByUserId_shouldThrows_ScoreException() {
        UserScore score = UserScore.builder()
                .userId(Long.MAX_VALUE)
                .score(BigInteger.ZERO)
                .build();

        userScoreService.createUserScore(score);

        var toTest = userScoreService.readUserScoreByUserId(score.getUserId());

        assertEquals(score.getUserId(), toTest.getUserId());
        assertEquals(score.getScore(), toTest.getScore());
    }

    @Test
    @DisplayName("Should return UserScore by userId")
    void readUserByUserId() {
        UserScore score = UserScore.builder()
                .score(BigInteger.ZERO)
                .userId(Long.MAX_VALUE-2)
                .build();

        userScoreService.createUserScore(score);
        var toTest = userScoreService.readUserScoreByUserId(score.getUserId());

        assertEquals(score.getScore(), toTest.getScore());
        assertEquals(score.getUserId(), toTest.getUserId());
    }

    @Test
    @DisplayName("Throws ScoreException(SCORE_NOT_FOUND) when try patch not existing UserScore")
    void patchUser_shouldThrows_ScoreException() {
        userScoreRepository.deleteAll();
        UserScore score = UserScore.builder()
                .userId(1)
                .score(BigInteger.ZERO)
                .build();

        Exception exception = assertThrows(ScoreException.class, () ->
                userScoreService.patchUserScore(1, score));

        assertEquals(ScoreError.SCORE_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Should return patched UserScore")
    void patchUserScore() {
        UserScore score = UserScore.builder()
                .userId(Long.MAX_VALUE-3)
                .score(BigInteger.ZERO)
                .build();

        UserScore toPatch = UserScore.builder()
                        .userId(Long.MAX_VALUE-3)
                        .score(BigInteger.ONE)
                                .build();

        userScoreService.createUserScore(score);

        var toTest = userScoreService.patchUserScore(score.getUserId(), toPatch);

        assertEquals(BigInteger.ONE, toTest.getScore());
    }

    @Test
    @DisplayName("Should return score of cerfLevel")
    void mapMessageToScore() {
        UserScoreMessage messageA1 = new UserScoreMessage(1, 1, true, WordCerfLevel.A1);
        UserScoreMessage messageA2 = new UserScoreMessage(1, 1, true, WordCerfLevel.A2);
        UserScoreMessage messageB1 = new UserScoreMessage(1, 1, true, WordCerfLevel.B1);
        UserScoreMessage messageB2 = new UserScoreMessage(1, 1, true, WordCerfLevel.B2);
        UserScoreMessage messageC1 = new UserScoreMessage(1, 1, true, WordCerfLevel.C1);
        UserScoreMessage messageC2 = new UserScoreMessage(1, 1, true, WordCerfLevel.C2);

        userScoreService.mapMessageToScore(messageA1);
        BigInteger scoreA1 = userScoreService.readUserScoreByUserId(1).getScore();

        assertEquals(BigInteger.valueOf(5), scoreA1);


        userScoreService.mapMessageToScore(messageA2);
        BigInteger scoreA2 = userScoreService.readUserScoreById(1).getScore();

        assertEquals(scoreA1.add(BigInteger.valueOf(10)), scoreA2);


        userScoreService.mapMessageToScore(messageB1);
        BigInteger scoreB1 = userScoreService.readUserScoreById(1).getScore();

        assertEquals(scoreA2.add(BigInteger.valueOf(20)), scoreB1);

        userScoreService.mapMessageToScore(messageB2);
        BigInteger scoreB2 = userScoreService.readUserScoreById(1).getScore();

        assertEquals(scoreB1.add(BigInteger.valueOf(30)), scoreB2);

        userScoreService.mapMessageToScore(messageC1);
        BigInteger scoreC1 = userScoreService.readUserScoreById(1).getScore();

        assertEquals(scoreB2.add(BigInteger.valueOf(50)), scoreC1);

        userScoreService.mapMessageToScore(messageC2);
        BigInteger scoreC2 = userScoreService.readUserScoreById(1).getScore();

        assertEquals(scoreC1.add(BigInteger.valueOf(100)), scoreC2);
    }
}