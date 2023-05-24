package me.gargant.events;

import org.bukkit.event.EventHandler;

import masecla.mlib.classes.Registerable;
import masecla.mlib.main.MLib;
import me.gargant.services.RunService;

public class PlayerQuitEvent extends Registerable {

    private RunService runService;

    public PlayerQuitEvent(MLib lib, RunService runService) {
        super(lib);
        this.runService = runService;
    }

    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent ev) {
        runService.removeRunning(ev.getPlayer().getUniqueId());
    }
}
