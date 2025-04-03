package org.wierzbadev.wordservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wierzbadev.wordservice.model.Language;
import org.wierzbadev.wordservice.model.UserWord;
import org.wierzbadev.wordservice.model.Word;
import org.wierzbadev.wordservice.service.ReviewService;
import org.wierzbadev.wordservice.utils.TokenConvert;

import java.util.List;

@RestController
@RequestMapping("/api/user/review")
public class ReviewController {
    private final ReviewService service;
    private final TokenConvert tokenConvert;

    public ReviewController(ReviewService service, TokenConvert tokenConvert) {
        this.service = service;
        this.tokenConvert = tokenConvert;
    }

    @GetMapping("/lesson")
    public ResponseEntity<List<Word>> readLessonWords(@RequestParam int limit) {
        long userId = tokenConvert.getLongIdFromToken();
        return ResponseEntity.ok(service.getNewWordsForLesson(userId, limit));
    }

    @GetMapping("/words")
    public ResponseEntity<List<UserWord>> readWordsForReview() {
        long userId = tokenConvert.getLongIdFromToken();
        return ResponseEntity.ok(service.readWordsForReview(userId));
    }

    @GetMapping("/words/{limit}")
    public ResponseEntity<List<UserWord>> readWordsForReview(@PathVariable("limit") int limit) {
        long userId = tokenConvert.getLongIdFromToken();
        return ResponseEntity.ok(service.readWordsForReview(userId, limit));
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkUserAnswer(@RequestParam long wordId,
                   @RequestParam String userAnswer, @RequestParam Language language) {
        long userId = tokenConvert.getLongIdFromToken();
        return ResponseEntity.ok(service.checkUserAnswer(userId, wordId, userAnswer, language));
    }
}
