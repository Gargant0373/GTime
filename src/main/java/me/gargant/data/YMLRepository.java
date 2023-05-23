package me.gargant.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import lombok.AllArgsConstructor;
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
        lib.getConfigurationAPI().getConfig("data")
                .set(uuid.toString().replace("-", "") + "." + time.getMap() + ".time", time.getTime());
        lib.getConfigurationAPI().getConfig("data")
                .set(uuid.toString().replace("-", "") + "." + time.getMap() + ".logged", System.currentTimeMillis());
        lib.getConfigurationAPI().saveConfig("data");
    }

    @Override
    public Time getTime(UUID uuid, String map) {
        ConfigurationSection section = getSection(uuid).getConfigurationSection(map);
        if(section == null) return null;
        return new Time(map, section.getLong("time"), section.getLong("logged"));
    }

    @Override
    public List<Time> getAllTimes(UUID uuid) {
        ConfigurationSection section = getSection(uuid);
        if(section == null) return new ArrayList<>();
        Set<String> registeredMaps = section.getKeys(false);
        List<Time> times = new ArrayList<>();
        for (String map : registeredMaps)
            times.add(new Time(map, section.getLong("time"), section.getLong("logged")));
        return times;
    }

    private ConfigurationSection getSection(UUID uuid) {
        ConfigurationSection section = lib.getConfigurationAPI().getConfig("data")
                .getConfigurationSection(uuid.toString().replace("-", ""));
        if (section == null) {
            lib.getConfigurationAPI().getConfig("data").set(uuid.toString().replace("-", ""), "");
            section = lib.getConfigurationAPI().getConfig("data")
                    .getConfigurationSection(uuid.toString().replace("-", ""));
        }
        return section;
    }
}
