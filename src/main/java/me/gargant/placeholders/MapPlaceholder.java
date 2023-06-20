package me.gargant.placeholders;

import org.bukkit.OfflinePlayer;

import masecla.mlib.classes.RegisterablePlaceholder;
import masecla.mlib.main.MLib;
import me.gargant.classes.Time;
import me.gargant.data.DataRepository;

public class MapPlaceholder extends RegisterablePlaceholder {

    private DataRepository dataRepository;

    public MapPlaceholder(MLib lib, DataRepository dataRepository) {
        super(lib);
        this.dataRepository = dataRepository;
    }

    @Override
    public String getIdentifier() {
        return "best_time";
    }

    @Override
    public String getPlaceholder(OfflinePlayer target, String identifier) {
        String[] args = identifier.split("_");
        String map = args[args.length - 1];
        Time time = dataRepository.getTime(target.getUniqueId(), map);
        if(time == null || time.getTime() == 0) return "null";
        return time.toString();
    }

}
