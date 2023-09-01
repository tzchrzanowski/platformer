package util;

public class Time {
    // initialized when application starts. Tells at what time game started.
    public static float timeStarted = System.nanoTime();

    /*
    * Get how much time has elapsed. nanoTime is current time.
    * 1E-9 converts time elapsed to nanoseconds.
    */
    public static float getTime() {
        return (float) ((System.nanoTime() - timeStarted) * 1E-9);
    }
}
