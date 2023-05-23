package me.gargant.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import masecla.mlib.annotations.RegisterableInfo;
import masecla.mlib.annotations.SubcommandInfo;
import masecla.mlib.classes.Registerable;
import masecla.mlib.classes.Replaceable;
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
        for (Player p : Bukkit.getOnlinePlayers())
            lib.getMessagesAPI().sendMessage("times.finished", p, true, new Replaceable("%map%", end.getMap()),
                    new Replaceable("%time%", end.toString()), new Replaceable("%player_name%", player.getName()));

        if (end.isRecord())
            lib.getMessagesAPI().sendMessage("times.personal-best", player, true, new Replaceable("%time%", end.getReadeableDifference()));
    }

    @SubcommandInfo(subcommand = "start", permission = "gtime.use")
    public void handleStart(Player player, String map) {
        runService.startRun(player.getUniqueId(), map);
    }
}
