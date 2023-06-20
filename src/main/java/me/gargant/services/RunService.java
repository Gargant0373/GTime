package me.gargant.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import masecla.mlib.classes.Registerable;
import masecla.mlib.main.MLib;
import me.gargant.classes.RunEnd;
import me.gargant.classes.Time;
import me.gargant.data.DataRepository;

public class RunService extends Registerable {

    private DataRepository dataRepository;

    public RunService(MLib lib, DataRepository dataRepository) {
        super(lib);
        this.dataRepository = dataRepository;
    }

    private int NUMBER_OF_CIRCLES = 10;

    private int taskId = -1;
    private Map<UUID, Time> running = new HashMap<>();
    /* Cache the previous times when running. */
    private Map<UUID, Time> previousTimes = new HashMap<>();

    private int startTask() {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(lib.getPlugin(), () -> running.forEach(this::sendMessage), 0,
                20);
    }

    public void startRun(UUID uuid, String map) {
        if (taskId == -1)
            taskId = startTask();
        running.put(uuid, new Time(map, System.currentTimeMillis()));
        previousTimes.put(uuid, dataRepository.getTime(uuid, map));
    }

    public RunEnd endRun(UUID uuid) {
        Time time = running.get(uuid);
        time.setTime(System.currentTimeMillis() - time.getTime());
        running.remove(uuid);
        previousTimes.remove(uuid);

        if (running.isEmpty()) {
            Bukkit.getScheduler().cancelTask(taskId);
            this.taskId = -1;
        }

        Time previousTime = dataRepository.getTime(uuid, time.getMap());
        if (previousTime == null || previousTime.getTime() > time.getTime())
            dataRepository.saveTime(uuid, time);

        return new RunEnd(lib, time.getMap(), time.getTime(), previousTime == null ? null : previousTime.getTime());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        this.running.remove(ev.getPlayer().getUniqueId());
        this.previousTimes.remove(ev.getPlayer().getUniqueId());
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
    private void sendMessage(UUID uuid, Time t) {
        String time = getCurrentTime(uuid);
        long diff = (System.currentTimeMillis() - t.getTime()) / 1000;
        Time previousTime = previousTimes.get(uuid);
        String previousTimeFormatted = previousTime == null ? "N/A" : previousTime.toString();
        lib.getMessagesAPI().sendActionbarMessage(
                "&f" + previousTimeFormatted + " " + generateCircles(diff, previousTime) + " " + " &f" + time,
                Bukkit.getPlayer(uuid));
    }

    /**
     * Returns the current time of the player in their run.
     * @param uuid the {@link UUID} to get the time from
     * @return the current time of the player in their run
     */
    public String getCurrentTime(UUID uuid) {
        Time t = running.get(uuid);
        if(t == null) return "N/A";
        long diff = (System.currentTimeMillis() - t.getTime());
        
        long s = (diff / 1000) % 60;
        long m = (diff / (1000 * 60)) % 60;
        
        return String.format("%02d:%02d", m, s);
    }
}
