package org.wierzbadev.scoreservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wierzbadev.scoreservice.model.UserScore;
import org.wierzbadev.scoreservice.model.dto.ranking.RankedUserProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
    Optional<UserScore> findByUserId(long userId);
    @Query(nativeQuery = true, value = """
            WITH ranked_users AS (
                SELECT
                    user_score.id,
                    user_score.user_id,
                    user_score.score,
                    RANK() OVER (ORDER BY user_score.score DESC) AS ranking_position
                FROM user_score
            )
            SELECT * FROM ranked_users WHERE ranking_position <= 20
            UNION ALL
            SELECT * FROM ranked_users WHERE user_id =:userId AND ranking_position > 20;
            
            """)
    List<RankedUserProjection> findByTop20(@Param("userId") long userId);
}
