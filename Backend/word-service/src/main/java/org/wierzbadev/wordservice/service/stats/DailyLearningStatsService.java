package org.wierzbadev.wordservice.service.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wierzbadev.wordservice.model.DailyLearningStats;
import org.wierzbadev.wordservice.model.LearningStatsArchive;
import org.wierzbadev.wordservice.model.dto.UserLearningDto;
import org.wierzbadev.wordservice.repository.DailyLearningRepository;
import org.wierzbadev.wordservice.repository.LearningStatsArchiveRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DailyLearningStatsService {

    private final DailyLearningRepository repository;
    private final LearningStatsArchiveRepository archiveRepository;

    public DailyLearningStatsService(DailyLearningRepository repository, LearningStatsArchiveRepository archiveRepository) {
        this.repository = repository;
        this.archiveRepository = archiveRepository;
    }

    public List<DailyLearningStats> readUsersWhoLearnedToday() {
        return repository.findByCreatedAtLessThanEqual(LocalDate.now().minusDays(1));
    }

    @Transactional
    public void updateUserStats(long userId, boolean isCorrect) {
        log.info("Updated stats for user: {}", userId);

        LocalDate today = LocalDate.now();

        if (isCorrect) {
            repository.upsertDailyLearningStats(userId, today, 1, 0);
        } else {
            repository.upsertDailyLearningStats(userId, today, 0, 1);
        }
    }

    public void deleteUserStats(DailyLearningStats stats) {
        createArchiveData(stats);
        log.info("Deleted stats for userId: {}", stats.getUserId());
        repository.delete(stats);
    }

    public void createArchiveData(DailyLearningStats stats) {
        LearningStatsArchive archive = LearningStatsArchive.builder()
                .userId(stats.getUserId())
                .totalCorrect(stats.getTotalCorrect())
                .totalWrong(stats.getTotalWrong())
                .createdAt(stats.getCreatedAt())
                .audit(LocalDateTime.now())
                .build();

        log.info("Created archive data for userId: {}", stats.getUserId());
        archiveRepository.save(archive);
    }

    @Transactional
    public List<UserLearningDto> sendUserStats() {
        List<DailyLearningStats> users = readUsersWhoLearnedToday();
        List<UserLearningDto> dtoList = new ArrayList<>();

        users.forEach(stats -> {
            deleteUserStats(stats);
            dtoList.add(new UserLearningDto(
                    stats.getUserId(),
                    stats.getTotalWrong(),
                    stats.getTotalCorrect(),
                    stats.getTotalCorrect() + stats.getTotalWrong()
            ));
        });

        log.info("Returned list of DailyLearningDto with size: {}", dtoList.size());
        return dtoList;
    }
}
