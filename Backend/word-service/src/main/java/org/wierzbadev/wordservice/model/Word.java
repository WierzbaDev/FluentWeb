package org.wierzbadev.wordservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Word's name must not be empty")
    @JsonProperty("sourceWord")
    private String sourceWord;
    @Convert(converter = MapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<Language, String> translation;
    @JsonProperty("wordCerfLevel")
    @NotNull(message = "Word's wordCerfLevel must not be empty")
    private WordCerfLevel wordCerfLevel;
}
