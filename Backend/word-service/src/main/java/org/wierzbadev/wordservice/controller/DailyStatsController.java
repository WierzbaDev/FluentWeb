package org.wierzbadev.wordservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wierzbadev.wordservice.service.stats.DailyLearningStatsService;
import org.wierzbadev.wordservice.service.stats.SendStatsInfoService;

@RestController
@RequestMapping("/api/stats")
public class DailyStatsController {

    private final SendStatsInfoService infoService;
    private final DailyLearningStatsService statsService;

    public DailyStatsController(SendStatsInfoService infoService, DailyLearningStatsService statsService) {
        this.infoService = infoService;
        this.statsService = statsService;
    }

    @GetMapping("/daily")
    public ResponseEntity<Void> sendDailyUsersStats() {
        infoService.sendStatsInfo(statsService.sendUserStats());
        return ResponseEntity.noContent().build();
    }
}
