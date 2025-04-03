package org.wierzbadev.wordservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "learning_stats_archive")
public class LearningStatsArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private long userId;
    private int totalCorrect;
    private int totalWrong;
    private LocalDate createdAt;
    private LocalDateTime audit;
}
