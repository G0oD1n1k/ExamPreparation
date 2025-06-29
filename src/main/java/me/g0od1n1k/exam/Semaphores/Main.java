package me.g0od1n1k.exam.Semaphores;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static Main instance;

    /**
     * Коммерчесая фирма занимается посредничесой деятельностью по продаже
     * автомобилей и осуществляет часть переговоров по @value телефонным линиям
     */
    private final int telephoneLines = 3;
    /**
     * В среднем поступает @value звонков в час
     */
    private final int callOnHours = 75;
    /**
     * Среднее время предварительных переговоров справочного
     * хара тера составляет @value мин
     */
    private final int conversationTimeOut = 5;
    /**
     * Среднее время ожидания разговора составляет @value мин.
     */
    private final float waitTimeOut = 0.5f;
    /**
     * Вероятность отказа не превышала @value %
     */
    private final int probabilityFailure = 20;
    /**
     * Всего минут
     */
    private final int totalMinutes = 75;
    /**
     * Конкретное время в минутах
     */
    private int currentMinutes;

    private AtomicInteger totalCalls = new AtomicInteger(0);
    private AtomicInteger totalServedCalls = new AtomicInteger(0);
    private AtomicInteger totalLeftCalls = new AtomicInteger(0);

    private final Semaphore phoneLines = new Semaphore(telephoneLines, true);

    public void start() throws InterruptedException {
        while(currentMinutes <= totalMinutes) {
            int avgCalls = TimeUtils.generatePoissonRandomCalls(callOnHours);
            for(int i = 0; i < avgCalls; i++) {
                Call call = new Call(totalCalls.incrementAndGet(), currentMinutes, conversationTimeOut);
                call.start();
            }
            Thread.sleep(probabilityFailure);
            currentMinutes++;
        }

        while (totalCalls.get() > totalLeftCalls.get() + totalServedCalls.get()) {
            Thread.sleep(probabilityFailure);
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

    public int getProbabilityFailure() {
        return probabilityFailure;
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
