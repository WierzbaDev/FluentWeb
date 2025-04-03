package org.wierzbadev.wordservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wierzbadev.wordservice.model.DailyLearningStats;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface DailyLearningRepository extends JpaRepository<DailyLearningStats, Integer> {

    List<DailyLearningStats> findByCreatedAtLessThanEqual(LocalDate createdAtIsLessThan);

    @Modifying
    @Query(
            value = """
                    INSERT INTO daily_learning_stats (user_id, created_at, total_correct, total_wrong)
                    VALUES (:userId, :createdAt, :totalCorrect, :totalWrong) ON CONFLICT (user_id, created_at) DO UPDATE SET
                    total_correct = daily_learning_stats.total_correct + EXCLUDED.total_correct, 
                    total_wrong = daily_learning_stats.total_wrong + EXCLUDED.total_wrong
                     """,
            nativeQuery = true
    )
    void upsertDailyLearningStats(
            @Param("userId") Long userId,
            @Param("createdAt") LocalDate createdAt,
            @Param("totalCorrect") Integer totalCorrect,
            @Param("totalWrong") Integer totalWrong
    );
}
