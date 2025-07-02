package me.g0od1n1k.exam.JavaConcurrency.Port;

public class Main {
    public static void main(String[] args) {
        Port port = new Port(1000, 3, 400);

        new Ship("Ship-1", 300, 500, port, 200, 100).start();
        new Ship("Ship-2", 400, 600, port, 300, 50).start();
        new Ship("Ship-3", 200, 400, port, 150, 0).start();
        new Ship("Ship-4", 0, 300, port, 0, 200).start();
        new Ship("Ship-5", 500, 700, port, 400, 100).start();
    }
}
