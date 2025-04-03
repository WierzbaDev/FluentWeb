package org.wierzbadev.scoreservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wierzbadev.scoreservice.model.UserScore;
import org.wierzbadev.scoreservice.model.dto.ranking.RankingDto;
import org.wierzbadev.scoreservice.service.logic.RankingService;
import org.wierzbadev.scoreservice.service.logic.UserScoreService;
import org.wierzbadev.scoreservice.utils.TokenConvert;

import java.util.List;

@RestController
@RequestMapping("/api/userScore")
public class UserScoreController {

    private final UserScoreService service;
    private final RankingService rankingService;
    private final TokenConvert tokenConvert;

    public UserScoreController(UserScoreService service, RankingService rankingService, TokenConvert tokenConvert) {
        this.service = service;
        this.rankingService = rankingService;
        this.tokenConvert = tokenConvert;
    }

    @GetMapping
    public ResponseEntity<List<UserScore>> readUsersScore() {
        return ResponseEntity.ok(service.readAllUsersScore());
    }

    @GetMapping("/top")
    public ResponseEntity<List<RankingDto>> readTopUsers() {
        long id = tokenConvert.getLongIdFromToken();
        return ResponseEntity.ok(rankingService.readTopRanking(id));
    }
}
