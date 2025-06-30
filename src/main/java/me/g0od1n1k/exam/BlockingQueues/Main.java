package me.g0od1n1k.exam.BlockingQueues;

import me.g0od1n1k.exam.utils.TimeUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static Main instance;

    private AtomicInteger totalCars = new AtomicInteger(0);
    private AtomicInteger totalServedCars = new AtomicInteger(0);
    private AtomicInteger totalLeftCars = new AtomicInteger(0);

    /**
     * Available Stands for inspection
     */
    private int standsAvailableForInspection = 3;
    /**
     * Average number of serviced cars per hour
     */
    private int avgCarsInHours = 14;

    /**
     *
     */
    private int timePerCar = (int) (60 * 0.4d);
    /**
     * Just a few minutes
     */
    private int totalMinutes = 8 * 60;
    /**
     * The specific time in minutes
     */
    private int currentMinutes = 0;
    /**
     * Wait limit in minutes
     */
    private int waitLimit = 5;

    private int scaleFactorTreadSleep = 20;

    private BlockingQueue<Car> carBlockingQueue = new ArrayBlockingQueue<>(standsAvailableForInspection);

    public void start() throws InterruptedException {
        while (currentMinutes < totalMinutes) {
            int cars = TimeUtils.generatePoissonRandom(avgCarsInHours);
            for (int i = 0; i < cars; i++) {
                Car car = new Car(totalCars.incrementAndGet(), currentMinutes, timePerCar);
                car.start();
            }
            Thread.sleep(scaleFactorTreadSleep);
            currentMinutes++;
        }

        Thread.sleep((long) scaleFactorTreadSleep * waitLimit);
        display();
    }

    public void display() {
        int totalCars = this.totalCars.get();
        int totalServedCars = this.totalServedCars.get();
        int totalLeftCars = this.totalLeftCars.get();

        String repeatDelimiter = new String(new char[5]).replace('\0', '=');

        System.out.printf("%s Statistics %s%n", repeatDelimiter, repeatDelimiter);
        System.out.printf("Total cars: %d%n", totalCars);
        System.out.printf("Total served cars: %d%n", totalServedCars);
        System.out.printf("Total left cars: %d%n", totalLeftCars);

        double relative = totalCars == 0 ? 0 : (double) totalServedCars / (double) totalCars;
        double absolute = (double) totalServedCars / (double) this.totalMinutes;

        System.out.printf("Relative throughput: %.2f%% calls/min%n", relative);
        System.out.printf("Absolute throughput: %.2f calls/min%n", absolute);

    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    public BlockingQueue<Car> getCarBlockingQueue() {
        return carBlockingQueue;
    }

    public AtomicInteger getTotalCars() {
        return totalCars;
    }

    public AtomicInteger getTotalServedCars() {
        return totalServedCars;
    }

    public AtomicInteger getTotalLeftCars() {
        return totalLeftCars;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public int getCurrentMinutes() {
        return currentMinutes;
    }

    public int getWaitLimit() {
        return waitLimit;
    }

    public int getScaleFactorTreadSleep() {
        return scaleFactorTreadSleep;
    }

    public static void main(String[] args) {
        try {
            Main.getInstance().start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
