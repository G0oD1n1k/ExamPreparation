package me.g0od1n1k.exam.utils;

public class TimeUtils {

    public static int randomAvg(double percent, int avg) {
        double spread = percent * avg; // 20% от avg
        double min = avg - spread;
        double max = avg + spread;

        return (int) (min + Math.random() * (max - min));
    }

    public static String formatTimeOnMinutes(int minute) {
        return String.format("%02d:%02d", minute / 60, minute % 60);
    }

    public static int generatePoissonRandom(int averageCallsPerHour) {
        final double averageCallsPerMinute = averageCallsPerHour / 60.0;
        final double probabilityThreshold = Math.exp(-averageCallsPerMinute);

        double product = 1.0;
        int eventCount = 0;

        while (product > probabilityThreshold) {
            product *= Math.random();
            eventCount++;
        }

        return eventCount - 1;
    }
}
