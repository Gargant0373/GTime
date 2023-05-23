package me.gargant.commands;

import org.bukkit.entity.Player;

import masecla.mlib.annotations.RegisterableInfo;
import masecla.mlib.annotations.SubcommandInfo;
import masecla.mlib.classes.Registerable;
import masecla.mlib.main.MLib;
import me.gargant.classes.RunEnd;
import me.gargant.services.RunService;

@RegisterableInfo(command = "gtime")
public class GTimeCommand extends Registerable {

    private RunService runService;

    public GTimeCommand(MLib lib, RunService runService) {
        super(lib);
        this.runService = runService;
    }

    @SubcommandInfo(subcommand = "end", permission = "gtime.use")
    public void handleEnd(Player player) {
        RunEnd end = runService.endRun(player.getUniqueId());
        player.sendMessage("You finished the map " + end.getMap() + " in " + end.getTime() + "ms!");
        player.sendMessage("This run was " + end.getReadeableDifference());
    }

    @SubcommandInfo(subcommand = "start", permission = "gtime.use")
    public void handleStart(Player player, String map) {
        runService.startRun(player.getUniqueId(), map);
        player.sendMessage("started running.");
    }
}
