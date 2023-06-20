package me.gargant.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import masecla.mlib.annotations.RegisterableInfo;
import masecla.mlib.annotations.SubcommandInfo;
import masecla.mlib.classes.Registerable;
import masecla.mlib.classes.Replaceable;
import masecla.mlib.main.MLib;
import me.gargant.classes.LeaderboardItem;
import me.gargant.classes.RunEnd;
import me.gargant.containers.MapViewContainer;
import me.gargant.data.DataRepository;
import me.gargant.data.MojangApiRepository;
import me.gargant.services.RunService;
import net.md_5.bungee.api.ChatColor;

@RegisterableInfo(command = "gtime")
public class GTimeCommand extends Registerable {

    private RunService runService;
    private DataRepository dataRepository;
    private MojangApiRepository mojangApiRepository;

    public GTimeCommand(MLib lib, RunService runService, DataRepository dataRepository,
            MojangApiRepository mojangApiRepository) {
        super(lib);
        this.runService = runService;
        this.dataRepository = dataRepository;
        this.mojangApiRepository = mojangApiRepository;
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

    @SubcommandInfo(subcommand = "leaderboard", aliases = { "lb" }, permission = "gtime.leaderboard")
    public void handleLeaderboard(CommandSender sender, String map) {
        lib.getMessagesAPI().sendMessage("leaderboard.message.header", sender, new Replaceable("%map%", map));
        List<LeaderboardItem> items = dataRepository.getTopTimes(map);

        for (int i = 0; i < items.size(); i++) {
            LeaderboardItem item = items.get(i);
            String playerName = mojangApiRepository.getPlayerName(item.getUuid());

            if(playerName == null) {
                playerName = "Unknown";
            }

            lib.getMessagesAPI().sendMessage("leaderboard.message.body", sender,
                    new Replaceable("%pos%", i + 1 + ""),
                    new Replaceable("%player_name%", playerName),
                    new Replaceable("%time%", item.getTime()));
        }

        lib.getMessagesAPI().sendMessage("leaderboard.message.footer", sender);

    }
}
