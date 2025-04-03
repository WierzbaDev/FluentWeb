package org.wierzbadev.wordservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wierzbadev.wordservice.exception.wordExpection.WordError;
import org.wierzbadev.wordservice.exception.wordExpection.WordException;
import org.wierzbadev.wordservice.model.Word;
import org.wierzbadev.wordservice.model.dto.PageResponse;
import org.wierzbadev.wordservice.repository.UserWordRepository;
import org.wierzbadev.wordservice.repository.WordRepository;

@Slf4j
@Service
public class WordService {

    private final WordRepository repository;
    private final UserWordRepository userWordRepository;

    public WordService(WordRepository repository, UserWordRepository userWordRepository) {
        this.repository = repository;
        this.userWordRepository = userWordRepository;
    }

    public PageResponse<Word> readAllWords(Pageable pageable) {
        Page<Word> wordPage = repository.findAll(pageable);

        log.info("Showed page of Word with number: {}", pageable.getPageNumber());

        return new PageResponse<>(
                wordPage.getContent(),
                wordPage.getTotalElements(),
                wordPage.getTotalPages(),
                wordPage.getSize(),
                wordPage.getNumber()
        );
    }

    public Word readWordById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new WordException(WordError.WORD_NOT_FOUND));
    }

    @CacheEvict(value = "newWordsForLesson", allEntries = true)
    public Word createWord(Word source) {
        if (repository.existsBySourceWord(source.getSourceWord()))
            throw new WordException(WordError.WORD_ALREADY_EXISTS);
        repository.save(source);
        return source;
    }

    @CacheEvict(value = "words", key = "'allWords'")
    public Word putWord(long id, Word source) {
        if (source.getSourceWord() == null || source.getTranslation() == null || source.getWordCerfLevel() == null)
            throw new WordException(WordError.WORD_MISSING_REQUIRED_FIELDS);

        Word result = readWordById(id);
        result.setSourceWord(source.getSourceWord());
        result.setTranslation(source.getTranslation());
        result.setWordCerfLevel(source.getWordCerfLevel());
        repository.save(result);
        return result;
    }

    @CacheEvict(value = "words", key = "'allWords'")
    public Word patchWord(long id, Word source) {
        Word result = readWordById(id);

        validateWord(result, source);

        repository.save(result);
        return result;
    }

    @CacheEvict(value = "words", key = "'allWords'")
    @Transactional
    public void deleteWord(long id) {
        if (repository.existsById(id)) {
            userWordRepository.deleteByWord_Id(id);
            repository.deleteById(id);
        }
        else
            throw new WordException(WordError.WORD_NOT_FOUND);
    }

    private static void validateWord(Word fromDb, Word fromJson) {
        if (fromJson.getSourceWord() != null)
            fromDb.setSourceWord(fromJson.getSourceWord());
        if (fromJson.getTranslation() != null)
            fromDb.setTranslation(fromJson.getTranslation());
        if (fromJson.getWordCerfLevel() != null)
            fromDb.setWordCerfLevel(fromJson.getWordCerfLevel());
    }
}
