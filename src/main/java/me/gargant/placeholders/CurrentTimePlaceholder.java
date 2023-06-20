package me.gargant.placeholders;

import org.bukkit.OfflinePlayer;

import masecla.mlib.classes.RegisterablePlaceholder;
import masecla.mlib.main.MLib;
import me.gargant.services.RunService;

public class CurrentTimePlaceholder extends RegisterablePlaceholder {

    private RunService runService;
    
    public CurrentTimePlaceholder(MLib lib, RunService runService) {
        super(lib);
        this.runService = runService;
    }

    @Override
    public String getIdentifier() {
        return "current_time";
    }

    @Override
    public String getPlaceholder(OfflinePlayer target, String identifier) {
        return runService.getCurrentTime(target.getUniqueId());
    }
    
}
