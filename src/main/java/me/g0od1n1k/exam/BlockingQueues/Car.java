package me.g0od1n1k.exam.BlockingQueues;

import me.g0od1n1k.exam.utils.TimeUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Car extends Thread {

    private int id;
    private int arrival;
    private int duration;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    public Car(int id, int arrival, int duration) {
        this.id = id;
        this.arrival = arrival;
        this.duration = TimeUtils.randomAvg(0.25, duration);
    }

    @Override
    public void run() {
        System.out.printf("LOG:\t[GOT_A_CAR]\t[time: %s]\t:[id: %d]%n", TimeUtils.formatTimeOnMinutes(arrival), id);

        try {
            Main main = Main.getInstance();
            BlockingQueue<Car> carBlockingQueue = main.getCarBlockingQueue();
            if (carBlockingQueue.offer(this, (long) main.getWaitLimit() * main.getScaleFactorTreadSleep(), timeUnit)) {
                System.out.printf("LOG:\t[CAR_GOT_IN_LINE]\t[time: %s]\t:[id: %d]%n", TimeUtils.formatTimeOnMinutes(main.getCurrentMinutes()), id);
                while (true) {
                    Car car = carBlockingQueue.peek();
                    if (car == this) {
                        if (main.getCurrentMinutes() + duration > main.getTotalMinutes()) {
                            System.out.printf("LOG:\t[HE_LEFT]\t[time: %s]\t[id: %d] | Does not have time to serve before the end of working hours%n", TimeUtils.formatTimeOnMinutes(Main.getInstance().getCurrentMinutes()), id);
                            carBlockingQueue.take();
                            main.getTotalLeftCars().incrementAndGet();
                            break;
                        }
                        int startTime = main.getCurrentMinutes();
                        System.out.printf("LOG:\t[STARTED_MACHINE_MAINTENANCE]\t[time: %s]\t[id: %d] | [arrival: %s]\t[wait time: %s min]\t[completed: %s] %n",
                                TimeUtils.formatTimeOnMinutes(startTime), id, TimeUtils.formatTimeOnMinutes(arrival), (startTime - arrival), TimeUtils.formatTimeOnMinutes(startTime + duration));

                        Thread.sleep((long) main.getScaleFactorTreadSleep() * (duration + 1));

                        System.out.printf("LOG:\t[COMPLETED_MACHINE_MAINTENANCE]\t[time: %s]\t[id: %d] | [duration: %s]%n",
                                TimeUtils.formatTimeOnMinutes(main.getCurrentMinutes()), id, duration);

                        carBlockingQueue.take();
                        main.getTotalServedCars().incrementAndGet();
                        break;
                    }
                    Thread.sleep(main.getScaleFactorTreadSleep());
                }
            }
            else {
                System.out.printf("LOG:\t[INTERRUPTED_CAR_GOT_IN_LINE]\t[time: %s]\t[id: %d] | All lines are busy%n", TimeUtils.formatTimeOnMinutes(main.getCurrentMinutes()), id);
                main.getTotalLeftCars().incrementAndGet();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
