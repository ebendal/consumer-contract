package com.ebendal.example.provider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
class BookController {

    private final BookJpaRepository bookJpaRepository;

    @PostMapping(value = "")
    public ResponseEntity<Book> createBook(CreateBookDto book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookJpaRepository.save(Book.from(book)));
    }

    @GetMapping(value = "")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK).body(bookJpaRepository.findAll());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    static class CreateBookDto {
        private String title;
    }
}
