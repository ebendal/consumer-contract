package com.ebendal.example;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Name {

    public static final String PROVIDER = "book-service";
    public static final String CONSUMER = "library-service";
    public static final String INTERACTION = "create-book";

}
