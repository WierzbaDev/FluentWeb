package org.wierzbadev.wordservice.unit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.wierzbadev.wordservice.exception.wordExpection.WordError;
import org.wierzbadev.wordservice.exception.wordExpection.WordException;
import org.wierzbadev.wordservice.model.Word;
import org.wierzbadev.wordservice.model.Language;
import org.wierzbadev.wordservice.model.WordCerfLevel;
import org.wierzbadev.wordservice.repository.UserWordRepository;
import org.wierzbadev.wordservice.repository.WordRepository;
import org.wierzbadev.wordservice.service.WordService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WordServiceTest {

    @Mock
    private UserWordRepository mockUserWordRepository;

    @Mock
    private WordRepository mockWordRepository;

    @InjectMocks
    private WordService toTest;

    @Test
    @DisplayName("should throw WordException with WORD_NOT_FOUND when list is empty")
    void readWordById_should_throw_exception_wordNotFound() {
        when(mockWordRepository.findById(anyLong())).thenReturn(Optional.empty());


        var exception = catchThrowable(() -> toTest.readWordById(2));

        assertThat(exception)
                .isInstanceOf(WordException.class)
                .hasMessageContaining("does not exists");
    }

    @Test
    @DisplayName("should return word called potato")
    void readWordById_should_findWord() {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.PL, "Ziemniak");
        when(mockWordRepository.findById(anyLong())).thenReturn(Optional.of(new Word(2, "potato", map, WordCerfLevel.A1)));

        var result = toTest.readWordById(4);

        assertEquals("potato", result.getSourceWord());
    }

    @Test
    @DisplayName("should throw WordException when word already exists")
    void createWord_should_throwException_whenWordAlreadyExists() {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.PL, "potato");

        when(mockWordRepository.existsBySourceWord("potato")).thenReturn(true);


        WordException exception = assertThrows(WordException.class, () ->
                toTest.createWord(new Word(3, "potato", map, WordCerfLevel.A1)));

        assertEquals(WordError.WORD_ALREADY_EXISTS.getMessage(), exception.getWordError().getMessage());

        verify(mockWordRepository, never()).save(any(Word.class));
    }

    @Test
    @DisplayName("should create word")
    void createWord_should_createNewWord() {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.PL, "Ziemniak");
        when(mockWordRepository.save(any(Word.class))).thenReturn(new Word(2, "potato", map, WordCerfLevel.A1));


        var result = toTest.createWord(new Word(2, "potato", map, WordCerfLevel.A1));

        assertEquals("potato", result.getSourceWord());
    }

    @Test
    @DisplayName("should throw WordException when any field is null")
    void putWord_should_throw_WordException() {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.PL, "Ziemniak");
        Word word = new Word();
        when(mockWordRepository.findById(anyLong())).thenReturn(Optional.of(new Word(1, "potato", map, WordCerfLevel.A1)));


        WordException exception = assertThrows(WordException.class, () ->
                toTest.putWord(1, word));

        assertEquals(WordError.WORD_MISSING_REQUIRED_FIELDS.getMessage(), exception.getWordError().getMessage());
    }

    @Test
    @DisplayName("should update existing word with new translation and cerf level")
    void putWord_should_putExistsWord() {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.PL, "Ziemniak");

        Map<Language, String> putMap = new HashMap<>();
        putMap.put(Language.PL, "Arbuz");

        when(mockWordRepository.findById(anyLong())).thenReturn(Optional.of(new Word(1, "potato", map, WordCerfLevel.A1)));
        Word word = new Word(1, "watermelon", putMap, WordCerfLevel.A2);


        var result = toTest.putWord(1, word);

        assertEquals("watermelon", result.getSourceWord());
        assertEquals(putMap, result.getTranslation());
        assertEquals(WordCerfLevel.A2, result.getWordCerfLevel());
    }

    @Test
    @DisplayName("should patch existing word with new field or fields")
    void patchWord_should_patchExistingWord() {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.PL, "Ziemniak");

        Map<Language, String> putMap = new HashMap<>();
        putMap.put(Language.PL, "Arbuz");

        when(mockWordRepository.findById(anyLong())).thenReturn(Optional.of(new Word(1, "potato", map, WordCerfLevel.A1)));
        Word word = new Word();
        word.setWordCerfLevel(WordCerfLevel.C2);


        var result = toTest.patchWord(1, word);

        assertEquals(WordCerfLevel.C2, result.getWordCerfLevel());
    }
}