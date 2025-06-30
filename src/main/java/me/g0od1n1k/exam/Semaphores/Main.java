package me.g0od1n1k.exam.Semaphores;

import me.g0od1n1k.exam.utils.TimeUtils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static Main instance;

    /**
     * The commercial firm is engaged in intermediary activities for the sale of cars
     * and carries out part of the negotiations via @value telephone lines
     */
    private final int telephoneLines = 3;
    /**
     * On average, we receive @value calls per hour
     */
    private final int callOnHours = 75;
    /**
     * The average time for preliminary negotiations of a reference nature is @value min
     */
    private final int conversationTimeOut = 5;
    /**
     * The average waiting time for a conversation is @value min
     */
    private final float waitTimeOut = 0.5f;
    /**
     * The probability of failure did not exceed @value %
     */
    private final int scaleFactorTreadSleep = 20;
    /**
     * Just a few minutes
     */
    private final int totalMinutes = 75;
    /**
     * The specific time in minutes
     */
    private int currentMinutes;

    private AtomicInteger totalCalls = new AtomicInteger(0);
    private AtomicInteger totalServedCalls = new AtomicInteger(0);
    private AtomicInteger totalLeftCalls = new AtomicInteger(0);

    private final Semaphore phoneLines = new Semaphore(telephoneLines, true);

    public void start() throws InterruptedException {
        while(currentMinutes <= totalMinutes) {
            int avgCalls = TimeUtils.generatePoissonRandom(callOnHours);
            for(int i = 0; i < avgCalls; i++) {
                Call call = new Call(totalCalls.incrementAndGet(), currentMinutes, conversationTimeOut);
                call.start();
            }
            Thread.sleep(scaleFactorTreadSleep);
            currentMinutes++;
        }

        while (totalCalls.get() > totalLeftCalls.get() + totalServedCalls.get()) {
            Thread.sleep(scaleFactorTreadSleep);
        }

        display();
    }

    public void display() {
        final int totalCalls = this.totalCalls.get();
        final int totalServedCalls = this.totalServedCalls.get();
        final int totalLeftCalls = this.totalLeftCalls.get();
        final String repeatDelimiter = new String(new char[5]).replace("\0", "=");

        System.out.printf("%s Statistics %s%n", repeatDelimiter, repeatDelimiter);
        System.out.println("Total calls: " + totalCalls);
        System.out.println("Total served calls: " + totalServedCalls);
        System.out.println("Total left calls: " + totalLeftCalls);

        final double relative = totalServedCalls == 0 ? 0 : ((double)totalServedCalls / (double)totalCalls) * 100;
        final double absolute = (double) totalServedCalls / (double) totalMinutes;
        System.out.printf("Relative throughput: %.2f%% calls/min%n", relative);
        System.out.printf("Absolute throughput: %.2f calls/min%n", absolute);
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    public AtomicInteger getTotalCalls() {
        return totalCalls;
    }

    public AtomicInteger getTotalServedCalls() {
        return totalServedCalls;
    }

    public AtomicInteger getTotalLeftCalls() {
        return totalLeftCalls;
    }

    public Semaphore getPhoneLines() {
        return phoneLines;
    }

    public int getCurrentMinutes() {
        return currentMinutes;
    }

    public int getScaleFactorTreadSleep() {
        return scaleFactorTreadSleep;
    }

    public float getWaitTimeOut() {
        return waitTimeOut;
    }

    public static void main(String[] args) {
        try {
            Main.getInstance().start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
