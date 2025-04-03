package org.wierzbadev.scoreservice.model.dto.ranking;

import java.math.BigInteger;

public interface RankedUserProjection {
    long getId();
    long getUserId();
    BigInteger getScore();
    int getRankingPosition();
}
