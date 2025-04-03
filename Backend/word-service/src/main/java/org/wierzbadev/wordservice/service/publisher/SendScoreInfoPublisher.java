package org.wierzbadev.wordservice.service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.wierzbadev.wordservice.model.dto.UserScoreMessage;

@Slf4j
@Service
public class SendScoreInfoPublisher {


    private final RabbitTemplate rabbitTemplate;

    public SendScoreInfoPublisher(@Autowired @Qualifier("wordRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserInfoFromLesson(UserScoreMessage message) {
        String exchange = "user-score-exchange";
        String routing = "user.score";
        log.info("Send message to exchange: {} routing: {} userId: {}, wordId: {}, isCorrect: {}, cerfLevel: {}",
                exchange, routing, message.getUserId(), message.getWordId(), message.isCorrect(), message.getWordCerfLevel());
        rabbitTemplate.convertAndSend(exchange, routing, message);
    }
}
