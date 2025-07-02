package me.g0od1n1k.exam.JavaConcurrency.SmallLibrary;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Book> books = Arrays.asList(
                new Book(1, "Java Concurrency in Practice", true),
                new Book(2, "Effective Java", true),
                new Book(3, "Clean Code", true),
                new Book(4, "A rare manuscript", false),
                new Book(5, "An old book", false)
        );

        Library library = new Library();

        new Reader("Ivan", library,
                List.of(books.get(0)),
                List.of(books.get(3), books.get(4)))
                .start();

        new Reader("Mariya", library,
                List.of(books.get(1), books.get(2)),
                Collections.emptyList())
                .start();

        new Reader("Petya", library,
                List.of(books.get(3)), // It won't work - the book is just for the audience
                List.of(books.get(0), books.get(1)))
                .start();

        new Reader("Olga", library,
                Collections.emptyList(),
                List.of(books.get(2), books.get(4)))
                .start();
    }
}
