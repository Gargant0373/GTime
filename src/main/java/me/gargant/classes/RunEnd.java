package me.gargant.classes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RunEnd {
    private String map;
    private Long time;
    private Long previousTime;

    /**
     * Returns whether or not the current time is faster than the previous time.
     * @return
     */
    public boolean isRecord() {
        return previousTime == null || previousTime > time;
    }

    /**
     * Returns the time in a human readable format.
     */
    public String toString() {
        long ms = time % 1000;
        long s = (time / 1000) % 60;
        long m = (time / (1000 * 60)) % 60;
        long h = (time / (1000 * 60 * 60)) % 24;

        return (h == 0 ? "" : (h > 10 ? h : "0" + h) + ":") + "" + (m > 10 ? m : "0" + m) + ":" + (s > 10 ? s : "0" + s)
                + "." + ms;
    }

    /**
     * @return the difference between the current time and the previous time in a
     */
    public String getReadeableDifference() {
        long difference = Math.abs(time - (previousTime == null ? 0 : previousTime));
        long ms = difference % 1000;
        long s = (difference / 1000) % 60;
        long m = (difference / (1000 * 60)) % 60;
        long h = (difference / (1000 * 60 * 60)) % 24;

        return (h == 0 ? "" : (h > 10 ? h : "0" + h) + ":") + "" + (m > 10 ? m : "0" + m) + ":" + (s > 10 ? s : "0" + s)
                + "." + ms + " " + (isRecord() ? "faster" : "slower");
    }
}
