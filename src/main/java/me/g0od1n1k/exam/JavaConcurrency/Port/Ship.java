package me.g0od1n1k.exam.JavaConcurrency.Port;

import java.util.concurrent.TimeUnit;

public class Ship extends Thread {
    private final String name;
    private int currentContainers;
    private final int capacity;
    private final Port port;
    private final int toUnload;
    private final int toLoad;

    public Ship(String name, int currentContainers, int capacity,
                Port port, int toUnload, int toLoad) {
        this.name = name;
        this.currentContainers = currentContainers;
        this.capacity = capacity;
        this.port = port;
        this.toUnload = toUnload;
        this.toLoad = toLoad;
    }

    public String getShipName() {
        return name;
    }

    public int getCurrentContainers() {
        return currentContainers;
    }

    public void setCurrentContainers(int count) {
        currentContainers = count;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + " arrived at the port");

            if (port.dock()) {
                System.out.println(name + " moored");

                if (toUnload > 0) {
                    port.unload(this, toUnload);
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                }

                if (toLoad > 0) {
                    port.load(this, toLoad);
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                }

                port.leave();
                System.out.println(name + " sailed. Cargo: " + currentContainers + "/" + capacity);
            } else {
                System.out.println(name + " didn't wait for the pier and sailed away");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}