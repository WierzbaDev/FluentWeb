package org.wierzbadev.scoreservice.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.wierzbadev.scoreservice.exception.ScoreError;
import org.wierzbadev.scoreservice.exception.ScoreException;
import org.wierzbadev.scoreservice.service.auth.AuthService;
import org.wierzbadev.scoreservice.service.client.UserServiceClient;
import org.wierzbadev.scoreservice.service.logic.UserScoreService;

@Slf4j
@Service
public class UserEventListener {

    private final UserServiceClient client;
    private final AuthService authService;
    private final UserScoreService scoreService;

    public UserEventListener(UserServiceClient client, AuthService authService, UserScoreService scoreService) {
        this.client = client;
        this.authService = authService;
        this.scoreService = scoreService;
    }

    @CacheEvict(value = "userCache", key = "'user:' + #userId")
    @RabbitListener(queues = "user-updates", containerFactory = "userRabbitListenerContainerFactory")
    public void handleUserUpdatedEvent(String userId) {
        log.info("Listener receive notification: Updated user with id: {}", userId);
        long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}. Skipping event.", userId);
            return;
        }

        try {
            String token = "Bearer " + authService.getSystemToken();
            client.readUserById(id, token);
        } catch (ScoreException e) {
            if (e.getScoreError() == ScoreError.SCORE_NOT_FOUND) {
                log.warn("User {} not found, ignoring event.", userId);
                return;
            }
            log.error("ScoreException while processing user {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing user update event for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    @CacheEvict(value = "userCache", key = "'user:' + #userId")
    @RabbitListener(queues = "user-delete", containerFactory = "userRabbitListenerContainerFactory")
    public void handleUserDeletedEvent(String userId) {
        log.info("Listener got notification: Deleted user with id: {}", userId );
        long id;

        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            log.error("Invalid received notification: Deleted user with id: {}", userId);
            return;
        }

        try {
            scoreService.deleteScore(id);
        } catch (Exception e) {
            log.error("Error while deleting score for user: {}: {}", userId, e.getMessage());
        }
    }
}
