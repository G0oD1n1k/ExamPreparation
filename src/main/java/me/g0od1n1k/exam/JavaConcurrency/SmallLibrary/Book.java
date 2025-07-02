package me.g0od1n1k.exam.JavaConcurrency.SmallLibrary;

public class Book {
    private final int id;
    private final String title;
    private final boolean availableForHome;

    public Book(int id, String title, boolean availableForHome) {
        this.id = id;
        this.title = title;
        this.availableForHome = availableForHome;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isAvailableForHome() { return availableForHome; }

    @Override
    public String toString() {
        return title + " (" + (availableForHome ? "home" : "audience") + ")";
    }
}
