package me.g0od1n1k.exam.JavaConcurrency.SmallLibrary;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Library {
    private final Map<Book, Reader> issuedBooks = new ConcurrentHashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Condition condition = rwLock.writeLock().newCondition();

    public boolean takeBooks(Reader reader, List<Book> booksForHome, List<Book> booksForHall) {
        rwLock.writeLock().lock();
        try {
            for (Book book : booksForHome) {
                if (issuedBooks.containsKey(book) || !book.isAvailableForHome()) {
                    return false;
                }
            }
            for (Book book : booksForHall) {
                if (issuedBooks.containsKey(book)) {
                    return false;
                }
            }

            for (Book book : booksForHome) {
                issuedBooks.put(book, reader);
            }
            for (Book book : booksForHall) {
                issuedBooks.put(book, reader);
            }
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void returnBooks(List<Book> books) {
        rwLock.writeLock().lock();
        try {
            for (Book book : books) {
                issuedBooks.remove(book);
            }
            condition.signalAll();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean tryTakeBooksWithWait(Reader reader, List<Book> booksForHome, List<Book> booksForHall)
            throws InterruptedException {
        rwLock.writeLock().lock();
        try {
            int attempts = 3;
            while (attempts-- > 0) {
                if (takeBooks(reader, booksForHome, booksForHall)) {
                    return true;
                }
                condition.await(1, TimeUnit.SECONDS);
            }
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}