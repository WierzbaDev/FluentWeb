package org.wierzbadev.wordservice.integration.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.wierzbadev.wordservice.model.DailyLearningStats;
import org.wierzbadev.wordservice.model.LearningStatsArchive;
import org.wierzbadev.wordservice.model.dto.UserLearningDto;
import org.wierzbadev.wordservice.repository.DailyLearningRepository;
import org.wierzbadev.wordservice.repository.LearningStatsArchiveRepository;
import org.wierzbadev.wordservice.service.stats.DailyLearningStatsService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DailyLearningStatsServiceIntegrationTest extends BaseIT {

    @Autowired
    private DailyLearningStatsService service;

    @Autowired
    private DailyLearningRepository repository;

    @Autowired
    private LearningStatsArchiveRepository archiveRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Should return list of users who learnt today")
    void shouldReturn_listOfUsers() {
        repository.deleteAll();
        // given
        service.updateUserStats(1L, true);
        DailyLearningStats daily = repository.findById(1).get();
        daily.setCreatedAt(LocalDate.now().minusDays(1));
        repository.save(daily);

        // when
        List<DailyLearningStats> result = service.readUsersWhoLearnedToday();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should create archive data")
    void shouldCreateArchiveData() {
        DailyLearningStats stats = DailyLearningStats.builder()
                .userId(101)
                .createdAt(LocalDate.now())
                .totalWrong(1)
                .totalCorrect(1)
                .build();
        repository.save(stats);

        service.createArchiveData(stats);
        List<LearningStatsArchive> archive = archiveRepository.findByUserId(101);

        assertEquals(1, archive.size());
    }

    @Test
    @DisplayName("Should return list of UserLearningDto")
    void shouldReturn_UserLearningDto() {
        repository.deleteAll();
        // given
        service.updateUserStats(1L, true);
        DailyLearningStats daily = repository.findById(1).get();
        daily.setCreatedAt(LocalDate.now().minusDays(1));
        repository.save(daily);

        List<UserLearningDto> result = service.sendUserStats();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}