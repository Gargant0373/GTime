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

    private int taskId = -1;
    private Map<UUID, Time> running = new HashMap<>();

    private int startTask() {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(lib.getPlugin(), () -> {
            for(UUID uuid : running.keySet())
                if(Bukkit.getPlayer(uuid) == null)
                    running.remove(uuid);
            running.forEach((c,v) -> {
                long difference = System.currentTimeMillis() - v.getTime();
                long s = difference / 1000;
                long m = s / 60;
                String time = (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
                lib.getMessagesAPI().sendActionbarMessage("Running for: " + time, Bukkit.getPlayer(c));
            });
        }, 0, 20);
    }

    public void startRun(UUID uuid, String map) {
        if(taskId == -1)
            taskId = startTask();
        running.put(uuid, new Time(map, System.currentTimeMillis()));
    }

    public RunEnd endRun(UUID uuid) {
        Time time = running.get(uuid);
        time.setTime(System.currentTimeMillis() - time.getTime());
        running.remove(uuid);

        if(running.isEmpty()) {
            Bukkit.getScheduler().cancelTask(taskId);
            this.taskId = -1;
        }

        Time previousTime = dataRepository.getTime(uuid, time.getMap());
        if (previousTime == null || previousTime.getTime() < time.getTime())
            dataRepository.saveTime(uuid, time);

        return new RunEnd(time.getMap(), time.getTime(), previousTime == null ? null : previousTime.getTime());
    }
}
