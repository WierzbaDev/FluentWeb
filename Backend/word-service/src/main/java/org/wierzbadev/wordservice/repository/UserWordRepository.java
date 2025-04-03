package org.wierzbadev.wordservice.repository;


import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wierzbadev.wordservice.model.UserWord;
import org.wierzbadev.wordservice.model.Word;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserWordRepository extends JpaRepository<UserWord, Long> {
    List<UserWord> findByUserIdAndNextReviewBefore(long userId, LocalDate nextReviewBefore);

    @Query(value = """
    SELECT wd.*
    FROM word wd
             LEFT JOIN user_words uw ON wd.id = uw.word_id AND uw.user_id = :userId
    WHERE (uw.user_id IS NULL OR (uw.success_count < 1 AND uw.failed_count < 1))
    ORDER BY array_position(array['A1', 'A2', 'B1', 'B2', 'C1', 'C2'], wd.word_cerf_level::TEXT)
    LIMIT :limit;
    """, nativeQuery = true)
    List<Word> getNextLesson(@Param("limit") int limit, @Param("userId") long userId);

    List<UserWord> findAllByUserId(long userId);

    @Query("SELECT u FROM UserWord u WHERE u.userId = :userId AND u.nextReview < :nextReviewBefore")
    Page<UserWord> findByLimitUserIdAndNextReviewBefore(
            @Param("userId") Long userId,
            @Param("nextReviewBefore") LocalDate nextReviewBefore,
            Pageable pageable);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uw FROM UserWord uw WHERE uw.userId = :userId AND uw.word.id = :wordId")
    Optional<UserWord> findByUserIdAndWordInWithLock(@Param("userId") Long userId, @Param("wordId") long id);

    @Modifying
    @Query(
            value = "INSERT INTO user_words (user_id, word_id, ease_factor, interval_days, last_reviewed, next_review, repetition_count, success_count, failed_count) " +
                    "VALUES (:userId, :wordId, :easeFactor, :intervalDays, :lastReviewed, :nextReview, :repetitionCount, :successCount, :failedCount) " +
                    "ON CONFLICT (user_id, word_id) DO UPDATE SET " +
                    "ease_factor = EXCLUDED.ease_factor, " +
                    "interval_days = EXCLUDED.interval_days, " +
                    "last_reviewed = EXCLUDED.last_reviewed, " +
                    "next_review = EXCLUDED.next_review, " +
                    "repetition_count = EXCLUDED.repetition_count, " +
                    "success_count = EXCLUDED.success_count, " +
                    "failed_count = EXCLUDED.failed_count",
            nativeQuery = true
    )
    void upsertUserWord(
            @Param("userId") Long userId,
            @Param("wordId") Long wordId,
            @Param("easeFactor") Double easeFactor,
            @Param("intervalDays") Integer intervalDays,
            @Param("lastReviewed") LocalDate lastReviewed,
            @Param("nextReview") LocalDate nextReview,
            @Param("repetitionCount") Integer repetitionCount,
            @Param("successCount") Integer successCount,
            @Param("failedCount") Integer failedCount
    );

    void deleteByWord_Id(long wordId);
}
