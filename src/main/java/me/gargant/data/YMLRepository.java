package me.gargant.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import masecla.mlib.classes.MamlConfiguration;
import masecla.mlib.main.MLib;
import me.gargant.classes.LeaderboardItem;
import me.gargant.classes.Time;

@RequiredArgsConstructor
public class YMLRepository implements DataRepository {

    @NonNull
    private MLib lib;

    @Override
    public void initialize() {
        // Nothing really should happen here.
    }

    @Override
    public void closeConnection() {
        // Nothing really should happen here.
    }

    @Override
    public void saveTime(UUID uuid, Time time) {
        /// Cache updating
        if(cache.containsKey(time.getMap())) {
            List<LeaderboardItem> items = cache.get(time.getMap());

            /// Checking if the time is better than the last one
            if(items.get(items.size() - 1).getTime().getTime() > time.getTime()) {
                // Adding new one and sorting, then removing worst one.
                items.add(new LeaderboardItem(uuid, time));
                items.sort((a, b) -> (int) (a.getTime().getTime() - b.getTime().getTime()));
                items.remove(items.size() - 1);
            }
        }

        MamlConfiguration config = lib.getConfigurationAPI().getConfig("data");
        String path = time.getMap() + "." + uuid.toString();
        config.set(path + ".time", time.getTime());
        config.set(path + ".logged", System.currentTimeMillis());
        lib.getConfigurationAPI().saveConfig("data");
    }

    @Override
    public Time getTime(UUID uuid, String map) {
        ConfigurationSection section = getSection(map);
        if (section == null)
            return null;
        section = section.getConfigurationSection(uuid.toString());
        if (section == null)
            return null;
        Time t = new Time(map, section.getLong("time"), section.getLong("logged"));
        if (t.getTime() == 0)
            return null;
        return t;
    }

    @Override
    public List<Time> getAllTimes(UUID uuid) {
        List<String> maps = getMaps();

        List<Time> times = new ArrayList<>();
        for (String map : maps) {
            ConfigurationSection section = getSection(map);
            if (section == null)
                continue;
            section = section.getConfigurationSection(uuid.toString());
            if (section == null)
                continue;
            Time t = new Time(map, section.getLong("time"), section.getLong("logged"));
            if (t.getTime() == 0)
                continue;
            times.add(t);
        }
        return times;
    }

    private List<String> getMaps() {
        return lib.getConfigurationAPI().getConfig("data").getKeys(false).stream().collect(Collectors.toList());
    }

    private ConfigurationSection getSection(String map) {
        return lib.getConfigurationAPI().getConfig("data")
                .getConfigurationSection(map);
    }

    private Map<String, List<LeaderboardItem>> cache = new HashMap<>();
    private Map<String, Long> cacheTime = new HashMap<>();

    @Override
    public List<LeaderboardItem> getTopTimes(String map) {
        if (cache.containsKey(map)) {
            if (System.currentTimeMillis() - cacheTime.get(map) < cacheTime() * 1000) {
                return cache.get(map);
            }
            cache.remove(map);
            cacheTime.remove(map);
        }

        ConfigurationSection section = getSection(map);
        if (section == null)
            return new ArrayList<>();
        List<UUID> registeredPlayers = section.getKeys(false).stream().map(c -> UUID.fromString(c))
                .collect(Collectors.toList());

        TreeMap<Long, UUID> times = new TreeMap<>();

        for (UUID player : registeredPlayers) {
            times.put(getTime(player, map).getTime(), player);
        }

        List<LeaderboardItem> items = new ArrayList<>();
        for (int i = 0; i < leaderboardSize(); i++) {
            if (times.isEmpty())
                break;
            Long time = times.firstKey();
            UUID uuid = times.get(time);
            items.add(new LeaderboardItem(uuid, getTime(uuid, map)));
            times.remove(time);
        }

        cache.put(map, items);
        cacheTime.put(map, System.currentTimeMillis());
        return items;
    }

    private int cacheTime() {
        return lib.getConfigurationAPI().getConfig().getInt("leaderboard.cache-time", 300);
    }

    private int leaderboardSize() {
        return lib.getConfigurationAPI().getConfig().getInt("leaderboard.size", 10);
    }
}
