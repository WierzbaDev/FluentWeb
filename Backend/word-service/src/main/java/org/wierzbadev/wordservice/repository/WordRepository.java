package org.wierzbadev.wordservice.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wierzbadev.wordservice.model.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Word findBySourceWord(@NotBlank(message = "Word's name must not be empty") String sourceWord);

    boolean existsBySourceWord(@NotBlank(message = "Word's name must not be empty") String sourceWord);

}
