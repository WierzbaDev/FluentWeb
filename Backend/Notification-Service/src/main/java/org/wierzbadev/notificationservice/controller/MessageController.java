package org.wierzbadev.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.wierzbadev.notificationservice.service.auth.AuthService;
import org.wierzbadev.notificationservice.service.client.WordServiceClient;


@RestController
public class MessageController {

    private final WordServiceClient client;
    private final AuthService authService;

    public MessageController(WordServiceClient client, AuthService authService) {
        this.client = client;
        this.authService = authService;
    }

    @Scheduled(cron = "0 0 5 * * *", zone = "Europe/Warsaw")
    public ResponseEntity<Void> sendEmail() {
        client.sendDailyStatsInfo("Bearer " + authService.getSystemToken());
        return ResponseEntity.noContent().build();
    }
}
