package com.ebendal.example.provider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
class Book {

    private UUID id;
    private String title;

    static Book from(BookController.CreateBookDto createBookDto) {
        Book book = new Book();
        book.setTitle(createBookDto.getTitle());
        book.setId(UUID.randomUUID());
        return book;
    }
}
