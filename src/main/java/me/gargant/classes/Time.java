package me.gargant.classes;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    @Override
    public String toString() {
        long ms = time % 1000;
        long s = (time / 1000) % 60;
        long m = (time / (1000 * 60)) % 60;
        long h = (time / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d.%03d", h, m, s, ms);
    }

    public Time clone() {
        return new Time(map, time, logged);
    }

    public String getLogTimeString() {
        Date date = new Date(logged);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        return dateFormat.format(date);
    }
}
