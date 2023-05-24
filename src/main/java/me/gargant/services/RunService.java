package me.gargant.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import masecla.mlib.main.MLib;
import me.gargant.classes.RunEnd;
import me.gargant.classes.Time;
import me.gargant.data.DataRepository;

@RequiredArgsConstructor
public class RunService {

    @NonNull
    private MLib lib;
    @NonNull
    private DataRepository dataRepository;

    private int NUMBER_OF_CIRCLES = 10;

    private int taskId = -1;
    private Map<UUID, Time> running = new HashMap<>();

    private int startTask() {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(lib.getPlugin(), () -> {
            running.forEach((c, v) -> {
                String message = generateMessage(c, v);
                lib.getMessagesAPI().sendActionbarMessage(message, Bukkit.getPlayer(c));
            });
        }, 0, 20);
    }

    public void startRun(UUID uuid, String map) {
        if (taskId == -1)
            taskId = startTask();
        running.put(uuid, new Time(map, System.currentTimeMillis()));
    }

    public RunEnd endRun(UUID uuid) {
        Time time = running.get(uuid);
        time.setTime(System.currentTimeMillis() - time.getTime());
        running.remove(uuid);

        if (running.isEmpty()) {
            Bukkit.getScheduler().cancelTask(taskId);
            this.taskId = -1;
        }

        Time previousTime = dataRepository.getTime(uuid, time.getMap());
        if (previousTime == null || previousTime.getTime() > time.getTime())
            dataRepository.saveTime(uuid, time);

        return new RunEnd(lib, time.getMap(), time.getTime(), previousTime == null ? null : previousTime.getTime());
    }

    /**
     * This method does not handle any database time logic.
     * 
     * @param uuid the {@link UUID} of the player to remove.
     */
    public void removeRunning(UUID uuid) {
        running.remove(uuid);
    }

    /**
     * Calculates the amount of gray and green circles for the action bar message.
     */
    private String generateCircles(long time, Time previousTime) {
        long previousTimeValue = previousTime == null ? 1 : previousTime.getTime() / 1000;
        int greenCircles = Math.round(previousTime == null ? 0 : NUMBER_OF_CIRCLES * time / previousTimeValue);
        if (greenCircles > NUMBER_OF_CIRCLES)
            greenCircles = NUMBER_OF_CIRCLES;
        int grayCircles = NUMBER_OF_CIRCLES - greenCircles;
        String circles = "";

        for (int i = 0; i < greenCircles; i++)
            circles += "&a\u2B24";
        for (int i = 0; i < grayCircles; i++)
            circles += "&7\u2B24";

        return circles;
    }

    /**
     * Generates the action bar message for the running player
     */
    private String generateMessage(UUID uuid, Time t) {
        long diff = (System.currentTimeMillis() - t.getTime()) / 1000;
        long m = diff / 60;
        long s = diff - m * 60;
        // Make the seconds and minutes be padded with
        // 0s and displayed in the mm:ss format
        String timeFormatted = (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
        Time previousTime = dataRepository.getTime(uuid, t.getMap());
        String previousTimeFormatted = previousTime == null ? "N/A" : previousTime.toString();
        return "&f" + previousTimeFormatted + " " + generateCircles(diff, previousTime) + " " + " &f" + timeFormatted;
    }
}
