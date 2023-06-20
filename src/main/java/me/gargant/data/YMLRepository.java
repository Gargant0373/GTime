package me.gargant.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import lombok.AllArgsConstructor;
import masecla.mlib.classes.MamlConfiguration;
import masecla.mlib.main.MLib;
import me.gargant.classes.Time;

@AllArgsConstructor
public class YMLRepository implements DataRepository {

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
        MamlConfiguration config = lib.getConfigurationAPI().getConfig("data");
        String path = uuid.toString().replace("-", "") + "." + time.getMap();
        config.set(path + ".time", time.getTime());
        config.set(path + ".logged", System.currentTimeMillis());
        lib.getConfigurationAPI().saveConfig("data");
    }

    @Override
    public Time getTime(UUID uuid, String map) {
        ConfigurationSection section = getSection(uuid);
        if (section == null)
            return null;
        section = section.getConfigurationSection(map);
        if (section == null)
            return null;
        return new Time(map, section.getLong("time"), section.getLong("logged"));
    }

    @Override
    public List<Time> getAllTimes(UUID uuid) {
        ConfigurationSection section = getSection(uuid);
        if (section == null)
            return new ArrayList<>();
        Set<String> registeredMaps = section.getKeys(false);
        List<Time> times = new ArrayList<>();
        for (String map : registeredMaps)
            times.add(new Time(map, section.getLong(map + ".time"), section.getLong(map + ".logged")));
        return times;
    }

    private ConfigurationSection getSection(UUID uuid) {
        return lib.getConfigurationAPI().getConfig("data")
                .getConfigurationSection(uuid.toString().replace("-", ""));
    }
}
