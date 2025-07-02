package me.g0od1n1k.exam.JavaConcurrency.SmallLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Reader extends Thread {
    private final String name;
    private final Library library;
    private final List<Book> booksForHome;
    private final List<Book> booksForHall;
    private final List<Book> currentBooks = new CopyOnWriteArrayList<>();

    public Reader(String name, Library library, List<Book> booksForHome, List<Book> booksForHall) {
        this.name = name;
        this.library = library;
        this.booksForHome = booksForHome;
        this.booksForHall = booksForHall;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + " he wants to take books");

            List<Book> allRequestedBooks = new ArrayList<>();
            allRequestedBooks.addAll(booksForHome);
            allRequestedBooks.addAll(booksForHall);

            if (library.tryTakeBooksWithWait(this, booksForHome, booksForHall)) {
                currentBooks.addAll(allRequestedBooks);
                System.out.println(name + " took books: " + allRequestedBooks);

                System.out.println(name + " is reading...");
                Thread.sleep(new Random().nextInt(3000));

                library.returnBooks(allRequestedBooks);
                currentBooks.clear();
                System.out.println(name + " returned books: " + allRequestedBooks);
            } else {
                System.out.println(name + " couldn't take the books and leaves");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}