package org.wierzbadev.scoreservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false)
    private long userId;
    private BigInteger score;

    public BigInteger getScore() {
        return score != null ? score : BigInteger.ZERO;
    }
}
