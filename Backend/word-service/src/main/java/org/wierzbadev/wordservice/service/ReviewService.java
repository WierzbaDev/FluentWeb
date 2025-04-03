package org.wierzbadev.wordservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wierzbadev.wordservice.exception.wordExpection.WordError;
import org.wierzbadev.wordservice.exception.wordExpection.WordException;
import org.wierzbadev.wordservice.model.Language;
import org.wierzbadev.wordservice.model.UserWord;
import org.wierzbadev.wordservice.model.Word;
import org.wierzbadev.wordservice.model.dto.UserScoreMessage;
import org.wierzbadev.wordservice.repository.UserWordRepository;
import org.wierzbadev.wordservice.repository.WordRepository;
import org.wierzbadev.wordservice.service.publisher.SendScoreInfoPublisher;
import org.wierzbadev.wordservice.service.stats.DailyLearningStatsService;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {

    private final UserWordRepository repository;
    private final WordRepository wordRepository;
    private final SendScoreInfoPublisher infoService;
    private final DailyLearningStatsService statsService;


    public ReviewService(UserWordRepository repository, WordRepository wordRepository, SendScoreInfoPublisher infoService, DailyLearningStatsService statsService) {
        this.repository = repository;
        this.wordRepository = wordRepository;
        this.infoService = infoService;
        this.statsService = statsService;
    }

    public List<Word> getNewWordsForLesson(long userId, int limit) {
        return repository.getNextLesson(limit, userId);
    }

    public List<UserWord> readWordsForReview(Long userId) {
        return repository.findByUserIdAndNextReviewBefore(userId, LocalDate.now());
    }

    public List<UserWord> readWordsForReview(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<UserWord> result = repository.findByLimitUserIdAndNextReviewBefore(userId, LocalDate.now(), pageable);
        return result.getContent();
    }

    @Transactional
    public boolean checkUserAnswer(long userId, long wordId, String userAnswer, Language language) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }

        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new WordException(WordError.WORD_NOT_FOUND));

        boolean isCorrect = checkWordFromTargetLanguage(word, userAnswer, language);

        sendUserLessonInfoAsync(userId, wordId, isCorrect, word);

        updateUserStatsAsync(userId, isCorrect);

        UserWord userWord = repository.findByUserIdAndWordInWithLock(userId, wordId)
                .orElseGet(() -> {
                    UserWord newUserWord = new UserWord();
                    newUserWord.setUserId(userId);
                    newUserWord.setWord(word);
                    return newUserWord;
                });

        updateReview(userWord, isCorrect);

        repository.upsertUserWord(
                userWord.getUserId(),
                userWord.getWord().getId(),
                userWord.getEaseFactor(),
                userWord.getIntervalDays(),
                userWord.getLastReviewed(),
                userWord.getNextReview(),
                userWord.getRepetitionCount(),
                userWord.getSuccessCount(),
                userWord.getFailedCount()
        );

        return isCorrect;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendUserLessonInfoAsync(long userId, long wordId, boolean isCorrect, Word word) {
        infoService.sendUserInfoFromLesson(new UserScoreMessage(userId, wordId, isCorrect, word.getWordCerfLevel()));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserStatsAsync(long userId, boolean isCorrect) {
        statsService.updateUserStats(userId, isCorrect);
    }

    public void deleteUserWordByUserId(long id) {
        List<UserWord> result = repository.findAllByUserId(id);
        repository.deleteAll(result);
    }

    private boolean checkWordFromTargetLanguage(Word word, String userAnswer, Language language) {
        return switch (language) {
            case PL -> (word.getTranslation().get(Language.PL)).equalsIgnoreCase(userAnswer);
            case EN -> word.getSourceWord().equalsIgnoreCase(userAnswer);
        };
    }

    private void updateReview(UserWord userWord, boolean correct) {
        int newIntervalDays = userWord.getIntervalDays();
        double newEaseFactor = userWord.getEaseFactor();

        // algorithm SuperMemo
        if (correct) {
            newIntervalDays = (int) Math.round(newIntervalDays * newEaseFactor);
            newEaseFactor = newEaseFactor + (0.1 - (5 - 3) * (0.08 + (5 - 3) * 0.02));
            newEaseFactor = Math.max(newEaseFactor, 1.3);
            userWord.setSuccessCount(userWord.getSuccessCount() + 1);
        } else {
            newIntervalDays = 1;
            newEaseFactor = Math.max(newEaseFactor - 0.2, 1.3);
            userWord.setFailedCount(userWord.getFailedCount() + 1);
        }

        newIntervalDays = Math.min(newIntervalDays, 3650);

        userWord.setIntervalDays(newIntervalDays);
        userWord.setEaseFactor(newEaseFactor);
        userWord.setNextReview(LocalDate.now().plusDays(newIntervalDays));
    }
}
