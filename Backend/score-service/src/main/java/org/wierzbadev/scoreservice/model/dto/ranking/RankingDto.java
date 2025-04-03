package org.wierzbadev.scoreservice.model.dto.ranking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
public class RankingDto {
    private long position;
    private String name;
    private BigInteger score;
}
