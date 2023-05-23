package me.gargant.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Time {
    @NonNull
    private String map;
    @NonNull
    private Long time;
    private Long logged;

    /**
     * Returns the time in a human readable format.
     */
    public String toString() {
        long ms = time % 1000;
        long s = (time / 1000) % 60;
        long m = (time / (1000 * 60)) % 60;
        long h = (time / (1000 * 60 * 60)) % 24;

        return (h == 0 ? "" : (h > 10 ? h : "0" + h) + ":") + "" + (m > 10 ? m : "0" + m) +
            ":" + (s > 10 ? s : "0" + s) + "." + ms;
    }
}
