package org.wierzbadev.wordservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "user_words", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "word_id"})})
public class UserWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastReviewed = LocalDate.now();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate nextReview = LocalDate.now().plusDays(1);
    private int intervalDays = 1;
    private double easeFactor = 2.5;
    private int repetitionCount = 0;
    private int successCount = 0;
    private int failedCount = 0;
}
