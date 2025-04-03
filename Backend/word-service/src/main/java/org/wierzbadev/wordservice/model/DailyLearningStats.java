package org.wierzbadev.wordservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "daily_learning_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "created_at"})
})
public class DailyLearningStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private int totalCorrect;
    private int totalWrong;
    private LocalDate createdAt;
}
