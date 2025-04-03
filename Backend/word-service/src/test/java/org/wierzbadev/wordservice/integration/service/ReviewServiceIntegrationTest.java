package org.wierzbadev.wordservice.integration.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.wierzbadev.wordservice.exception.wordExpection.WordError;
import org.wierzbadev.wordservice.exception.wordExpection.WordException;
import org.wierzbadev.wordservice.model.Language;
import org.wierzbadev.wordservice.model.Word;
import org.wierzbadev.wordservice.model.WordCerfLevel;
import org.wierzbadev.wordservice.repository.WordRepository;
import org.wierzbadev.wordservice.service.ReviewService;
import org.wierzbadev.wordservice.service.WordService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class ReviewServiceIntegrationTest extends BaseIT {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private WordService wordService;

    @Autowired
    private WordRepository wordRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Should return list of words for a new lesson")
    void getNewLesson() {
        createWords();
        List<Word> words = reviewService.getNewWordsForLesson(Long.MAX_VALUE, 3);

        assertEquals(3, words.size());
    }

    @Test
    @DisplayName("Throws WordException(WORD_NOT_FOUND) when a word does not exists")
    void checkUserAnswer_throwsWordException() {
        Exception exception = assertThrows(WordException.class, () ->
                reviewService.checkUserAnswer(Long.MAX_VALUE-1, Long.MAX_VALUE-1, "Example", Language.PL));

        assertEquals(WordError.WORD_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Should return true when word exists")
    void checkUserAnswer() {
        Word word = Word.builder()
                .sourceWord("capitan")
                .translation(Map.of(Language.PL, "kapitan"))
                .wordCerfLevel(WordCerfLevel.A1)
                .build();
        wordService.createWord(word);
        // given
        long id = wordRepository.findBySourceWord("capitan").getId();

        // when
        boolean toTest = reviewService.checkUserAnswer(Long.MAX_VALUE-2, id, "kapitan", Language.PL);

        // then
        assertTrue(toTest);
    }

    private void createWords() {
        List<String> words = List.of("one", "two", "three");
        List<String> translations = List.of("jeden", "dwa", "trzy");

        for (int i = 0; i < 3; i++) {
            Word word = Word.builder()
                    .sourceWord(words.get(i))
                    .translation(Map.of(Language.PL, translations.get(i)))
                    .wordCerfLevel(WordCerfLevel.A1)
                    .build();
            wordService.createWord(word);
        }
    }
}