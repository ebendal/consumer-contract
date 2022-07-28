package com.ebendal.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Name {

    public static final String PROVIDER = "book-service";
    public static final String CONSUMER_ONE = "consumer-one";
    public static final String CONSUMER_TWO = "consumer-two";
    public static final String CREATE_BOOK_INTERACTION = "create-book";
    public static final String GET_ALL_BOOKS_INTERACTION = "get-all-books";

}
