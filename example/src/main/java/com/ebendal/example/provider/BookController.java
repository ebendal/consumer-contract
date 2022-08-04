package com.ebendal.example.provider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
class BookController {

    private final Map<UUID, Book> map = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody CreateBookDto createBookDto) {
        Book book = Book.from(createBookDto);
        map.put(book.getId(), book);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping
    public ResponseEntity<Collection<Book>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK).body(map.values());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") UUID id) {
        return Optional.ofNullable(map.get(id))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/{id}")
    public ResponseEntity<Book> checkId(@PathVariable("id") UUID pathId, @RequestBody IdCheckDto book, @RequestHeader("headerId") UUID headerId, @RequestParam("queryParameterId") UUID queryParameterId) {
        log.info("Path id: {}, headerId: {}, queryParameterId: {}, bodyId: {}", pathId, headerId, queryParameterId, book.getId());
        if (map.containsKey(pathId) && pathId.equals(headerId) && pathId.equals(queryParameterId) && pathId.equals(book.getId())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class CreateBookDto {
        private String title;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class IdCheckDto {
        private UUID id;
    }
}
