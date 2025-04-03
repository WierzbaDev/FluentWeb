package org.wierzbadev.scoreservice.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.wierzbadev.scoreservice.model.dto.user.UserScoreMessage;
import org.wierzbadev.scoreservice.service.logic.UserScoreService;

@Slf4j
@Service
public class ScoreEventListener {

    private final UserScoreService service;

    public ScoreEventListener(UserScoreService service) {
        this.service = service;
    }

    @RabbitListener(queues = "user-score")
    public void receiveUserInfo(UserScoreMessage message) {
        log.info("Receive message: UserId: {}, isCorrect: {}, cerfLevel: {}", message.getUserId(), message.isCorrect(), message.getWordCerfLevel());
        service.mapMessageToScore(message);
    }
}
