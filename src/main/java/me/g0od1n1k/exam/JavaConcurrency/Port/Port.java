package me.g0od1n1k.exam.JavaConcurrency.Port;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private final int capacity;
    private int currentContainers;
    private final Semaphore docks;
    private final Lock lock = new ReentrantLock();

    public Port(int capacity, int dockCount, int initialContainers) {
        this.capacity = capacity;
        this.currentContainers = initialContainers;
        this.docks = new Semaphore(dockCount, true);
    }

    public void unload(Ship ship, int amount) {
        lock.lock();
        try {
            int availableSpace = capacity - currentContainers;
            int unloadAmount = Math.min(Math.min(amount, ship.getCurrentContainers()), availableSpace);

            if (unloadAmount > 0) {
                ship.setCurrentContainers(ship.getCurrentContainers() - unloadAmount);
                currentContainers += unloadAmount;
                System.out.printf("%s unloaded %d containers. At port: %d/%d%n",
                        ship.getShipName(), unloadAmount, currentContainers, capacity);
            }
        } finally {
            lock.unlock();
        }
    }

    public void load(Ship ship, int amount) {
        lock.lock();
        try {
            int availableContainers = currentContainers;
            int freeSpace = ship.getCapacity() - ship.getCurrentContainers();
            int loadAmount = Math.min(Math.max(amount, freeSpace), availableContainers);

            if (loadAmount > 0) {
                ship.setCurrentContainers(ship.getCurrentContainers() + loadAmount);
                currentContainers -= loadAmount;
                System.out.printf("%s loaded %d containers. At port: %d/%d%n",
                        ship.getShipName(), loadAmount, currentContainers, capacity);
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean dock() throws InterruptedException {
        return docks.tryAcquire(1, TimeUnit.SECONDS);
    }

    public void leave() {
        docks.release();
    }
}
