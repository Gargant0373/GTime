package me.gargant.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import masecla.mlib.annotations.RegisterableInfo;
import masecla.mlib.annotations.SubcommandInfo;
import masecla.mlib.classes.Registerable;
import masecla.mlib.classes.Replaceable;
import masecla.mlib.main.MLib;
import me.gargant.GTime;
import me.gargant.classes.GTimeAPI;
import me.gargant.classes.RunEnd;
import me.gargant.containers.MapViewContainer;
import me.gargant.services.RunService;

@RegisterableInfo(command = "gtime")
public class GTimeCommand extends Registerable {

    private RunService runService;
    private GTimeAPI api = GTime.api();

    public GTimeCommand(MLib lib, RunService runService) {
        super(lib);
        this.runService = runService;
    }

    @SubcommandInfo(subcommand = "end", permission = "gtime.use")
    public void handleEnd(Player player) {
        RunEnd end = runService.endRun(player.getUniqueId());
        end.broadcastEndMessage(player);
    }

    @SubcommandInfo(subcommand = "start", permission = "gtime.use")
    public void handleStart(Player player, String map) {
        runService.startRun(player.getUniqueId(), map);
    }

    @SubcommandInfo(subcommand = "times", permission = "gtime.times")
    public void handleTimes(Player player) {
        lib.getContainerAPI().openFor(player, MapViewContainer.class);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // getRunService() returns a RunService instance
        api.getRunService().startRun(player.getUniqueId(), "myCoolMapName");
    }
}
