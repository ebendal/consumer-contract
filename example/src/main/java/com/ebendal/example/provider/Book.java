package com.ebendal.example.provider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
class Book {

    private UUID id;
    private String title;
    private UUID authorID;

    static Book from(BookController.CreateBookDto createBookDto) {
        Book book = new Book();
        book.setTitle(createBookDto.getTitle());
        book.setId(UUID.randomUUID());
        book.setAuthorID(createBookDto.getAuthorID());
        return book;
    }
}
