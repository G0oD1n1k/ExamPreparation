package me.g0od1n1k.exam.Semaphores;

import me.g0od1n1k.exam.utils.TimeUtils;

import java.util.concurrent.TimeUnit;

public class Call extends Thread {

    private final int id;
    private final int arrival;
    private final int duration;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    public Call(int id, int arrival, int duration) {
        this.id = id;
        this.arrival = arrival;
        this.duration = TimeUtils.randomAvg(0.2d, duration);
    }

    @Override
    public void run() {
        System.out.printf("LOG:\t[GOT_A_CALL]\t[time: %s]\t[id: %d]%n", TimeUtils.formatTimeOnMinutes(arrival), id);

        final Main main = Main.getInstance();
        try {
            if (main.getPhoneLines().tryAcquire((int) (main.getWaitTimeOut() * main.getScaleFactorTreadSleep()), timeUnit)) {
                int startCurrentMinutes = main.getCurrentMinutes();

                System.out.printf("LOG:\t[IN_A_CALL]\t[time: %s]\t[id: %d] | [duration: %d min]%n", TimeUtils.formatTimeOnMinutes(startCurrentMinutes), id, duration);

                Thread.sleep((long) duration * main.getScaleFactorTreadSleep());

                System.out.printf("LOG:\t[COMPLETED_A_CALL]\t[time: %s]\t[id: %d]%n", TimeUtils.formatTimeOnMinutes(startCurrentMinutes), id);

                main.getTotalServedCalls().incrementAndGet();
                main.getPhoneLines().release();
            } else {
                System.out.printf("LOG:\t[INTERRUPTED_A_CALL]\t[time: %s]\t[id: %d] | All lines are busy%n", TimeUtils.formatTimeOnMinutes(main.getCurrentMinutes()), id);
                main.getTotalLeftCalls().incrementAndGet();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
