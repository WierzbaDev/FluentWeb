package org.wierzbadev.scoreservice.service.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.wierzbadev.scoreservice.model.dto.ranking.RankedUserProjection;
import org.wierzbadev.scoreservice.model.dto.ranking.RankingDto;
import org.wierzbadev.scoreservice.model.dto.user.UserDto;
import org.wierzbadev.scoreservice.service.auth.AuthService;
import org.wierzbadev.scoreservice.service.client.UserServiceClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RankingService {
    private final UserServiceClient client;
    private final UserScoreService scoreService;
    private final AuthService authService;
    private final CacheManager cacheManager;

    public RankingService(UserServiceClient client, UserScoreService scoreService, AuthService authService, CacheManager cacheManager) {
        this.client = client;
        this.scoreService = scoreService;
        this.authService = authService;
        this.cacheManager = cacheManager;
    }

    public List<RankingDto> readTopRanking(long id) {
        log.info("User with id: {}, read top ranking", id);

        List<RankedUserProjection> projections = scoreService.readByTop20(id);

        List<Long> userIds = projections.stream()
                .map(RankedUserProjection::getUserId)
                .distinct()
                .toList();

        Cache cache = cacheManager.getCache("userCache");
        Map<Long, String> userNames = new HashMap<>();
        List<Long> missingUserIds = new ArrayList<>();

        for (Long userId: userIds) {
            String cachedUsername = cache.get("user:" + userId, String.class);
            if (cachedUsername != null) {
                userNames.put(userId, cachedUsername);
            } else {
                missingUserIds.add(userId);
            }
        }

        if (!missingUserIds.isEmpty()) {
            List<UserDto> fetchedUsers = client.readUsersByIds("Bearer " + authService.getSystemToken(), missingUserIds);

            for (UserDto user: fetchedUsers) {
                userNames.put(user.getId(), user.getName());
                cache.put("user:" + user.getId(), user.getName());
            }
        }

        return projections.stream()
                .map(proj -> new RankingDto(proj.getRankingPosition(), userNames.get(proj.getUserId()), proj.getScore()))
                .toList();
    }
}
