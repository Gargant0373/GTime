package me.gargant.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import masecla.mlib.annotations.RegisterableInfo;
import masecla.mlib.annotations.SubcommandInfo;
import masecla.mlib.classes.Registerable;
import masecla.mlib.main.MLib;
import me.gargant.classes.RunEnd;
import me.gargant.containers.MapViewContainer;
import me.gargant.services.RunService;
import net.md_5.bungee.api.ChatColor;

@RegisterableInfo(command = "gtime")
public class GTimeCommand extends Registerable {

    private RunService runService;

    public GTimeCommand(MLib lib, RunService runService) {
        super(lib);
        this.runService = runService;
    }

    @Override
    public void fallbackCommand(CommandSender sender, String[] args) {
        String message = "&fRunning &c&lG&e&lTime &fv" + lib.getPlugin().getDescription().getVersion();
        message = ChatColor.translateAlternateColorCodes('&', message);
        String help = "&f/gtime &chelp?";
        help = ChatColor.translateAlternateColorCodes('&', help);

        sender.sendMessage(message);
        sender.sendMessage(help);
    }

    @SubcommandInfo(subcommand = "help", permission = "gtime.help")
    public void handleHelp(Player player) {
        List<String> help = new ArrayList<>();
        help.add("&c&lG&e&lTime &fv" + lib.getPlugin().getDescription().getVersion());
        help.add("&c/gtime &estart <map> &7- Start a run");
        help.add("&c/gtime &eend &7- End a run");
        help.add("&c/gtime &etimes &7- View your times");
        help.add("&c/gtime &ehelp &7- View this help page");

        help.forEach(c -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', c)));
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
}
