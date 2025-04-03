package org.wierzbadev.wordservice.integration.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.wierzbadev.wordservice.exception.wordExpection.WordError;
import org.wierzbadev.wordservice.exception.wordExpection.WordException;
import org.wierzbadev.wordservice.model.Language;
import org.wierzbadev.wordservice.model.Word;
import org.wierzbadev.wordservice.model.WordCerfLevel;
import org.wierzbadev.wordservice.repository.WordRepository;
import org.wierzbadev.wordservice.service.WordService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class WordServiceIntegrationTest extends BaseIT {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordService wordService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Throws WordException(WORD_ALREADY_EXISTS) when sourceWord is already in database")
    void createWord_shouldThrow_WordException() {
        // given
        Map<Language, String> map = new HashMap<>();
        map.put(Language.PL, "jakiś");

        Word existingWord = Word.builder()
                .sourceWord("any")
                .translation(map)
                .wordCerfLevel(WordCerfLevel.A1)
                        .build();

        wordService.createWord(existingWord);
        Word word = Word.builder()
                .sourceWord("any")
                .translation(map)
                .wordCerfLevel(WordCerfLevel.A1)
                .build();

        // when
        Exception exception = assertThrows(WordException.class, () ->
                wordService.createWord(word));

        // then
        assertEquals(WordError.WORD_ALREADY_EXISTS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Should create a new word")
    void should_createNewWord() {
        // given
        Word toCreate = Word.builder()
                .sourceWord("angry")
                .translation(Map.of(Language.PL, "Zły"))
                .wordCerfLevel(WordCerfLevel.A1)
                .build();

        // when
        var toTest = wordService.createWord(toCreate);
        Word result = wordRepository.findBySourceWord("angry");

        // then
        assertEquals(toCreate.getSourceWord(), result.getSourceWord());
        assertEquals(toCreate.getWordCerfLevel(), result.getWordCerfLevel());
        assertEquals(toCreate.getTranslation(), result.getTranslation());
    }

    @Test
    @DisplayName("Throws WordException(WORD_MISSING_REQUIRED_FIELDS) when these fields are null")
    void putWord_shouldThrow_WordException() {
        // given
        Word word = Word.builder()
                .sourceWord("Exception")
                .translation(Map.of(Language.PL, "Wyjątek"))
                .wordCerfLevel(WordCerfLevel.A2)
                .build();

        wordService.createWord(word);
        long id = wordRepository.findBySourceWord(word.getSourceWord()).getId();

        Word put = Word.builder()
                .sourceWord(null)
                .translation(null)
                .wordCerfLevel(null)
                .build();

        // when
        Exception exception = assertThrows(WordException.class, () ->
                wordService.putWord(id, put));

        // then
        assertEquals(WordError.WORD_MISSING_REQUIRED_FIELDS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Should put old word for new field's values")
    void putWord() {
        // given
        Word word = Word.builder()
                .sourceWord("Exception")
                .translation(Map.of(Language.PL, "Wyjątek"))
                .wordCerfLevel(WordCerfLevel.A2)
                .build();

        wordService.createWord(word);
        long id = wordRepository.findBySourceWord(word.getSourceWord()).getId();

        Word put = Word.builder()
                .sourceWord("Nothing")
                .translation(Map.of(Language.PL, "Nic"))
                .wordCerfLevel(WordCerfLevel.A1)
                .build();

        // when
        var toTest = wordService.putWord(id, put);

        assertEquals("Nothing", toTest.getSourceWord());
        assertEquals(Map.of(Language.PL, "Nic"), toTest.getTranslation());
        assertEquals(WordCerfLevel.A1, toTest.getWordCerfLevel());
    }

    @Test
    @DisplayName("Should patch old word for new field's values, but doesn't fields with null")
    void patchWord() {
        // given
        Word word = Word.builder()
                .sourceWord("Convert")
                .translation(Map.of(Language.PL, "konwertować"))
                .wordCerfLevel(WordCerfLevel.B1)
                .build();

        wordService.createWord(word);
        long id = wordRepository.findBySourceWord(word.getSourceWord()).getId();

        Word patch = Word.builder()
                .sourceWord("something")
                .translation(null)
                .wordCerfLevel(null)
                .build();

        // when
        var toTest = wordService.patchWord(id, patch);

        assertEquals("something", toTest.getSourceWord());
        assertEquals(Map.of(Language.PL, "konwertować"), toTest.getTranslation());
        assertEquals(WordCerfLevel.B1, toTest.getWordCerfLevel());
    }

    @Test
    @DisplayName("Should delete word by id")
    void deleteWord() {
        Word word = Word.builder()
                .sourceWord("delete")
                .translation(Map.of(Language.PL, "usuń"))
                .wordCerfLevel(WordCerfLevel.A1)
                .build();

        wordService.createWord(word);
        long id = wordRepository.findBySourceWord("delete").getId();
        wordService.deleteWord(id);

        Exception exception = assertThrows(WordException.class, () ->
                wordService.readWordById(id));

        assertEquals(WordError.WORD_NOT_FOUND.getMessage(), exception.getMessage());
    }
}