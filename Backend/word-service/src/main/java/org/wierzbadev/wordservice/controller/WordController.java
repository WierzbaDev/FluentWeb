package org.wierzbadev.wordservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wierzbadev.wordservice.model.Word;
import org.wierzbadev.wordservice.model.dto.PageResponse;
import org.wierzbadev.wordservice.service.WordService;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/words")
public class WordController {
    private final WordService service;

    public WordController(WordService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<Word>> readAllWords(
    @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.readAllWords(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Word> readWordById(@PathVariable long id) {
        return ResponseEntity.ok(service.readWordById(id));
    }

    @PostMapping
    public ResponseEntity<Word> createWord(@RequestBody Word word) {
        Word result = service.createWord(word);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Word> putWord(@PathVariable long id, @RequestBody @Valid Word word) {
        Word result = service.putWord(id, word);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Word> patchWord(@PathVariable long id, @RequestBody Word word) {
        Word result = service.patchWord(id, word);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable long id) {
        service.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}
