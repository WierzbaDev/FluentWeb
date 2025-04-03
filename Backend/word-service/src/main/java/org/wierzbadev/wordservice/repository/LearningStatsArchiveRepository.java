package org.wierzbadev.wordservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wierzbadev.wordservice.model.LearningStatsArchive;

import java.util.List;

@Repository
public interface LearningStatsArchiveRepository extends JpaRepository<LearningStatsArchive, Long> {
    List<LearningStatsArchive> findByUserId(long userId);
}
