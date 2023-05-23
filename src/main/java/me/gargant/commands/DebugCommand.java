package me.gargant.commands;

import org.bukkit.entity.Player;

import masecla.mlib.annotations.RegisterableInfo;
import masecla.mlib.annotations.SubcommandInfo;
import masecla.mlib.classes.Registerable;
import masecla.mlib.main.MLib;
import me.gargant.classes.Time;
import me.gargant.data.DataRepository;

@RegisterableInfo(command = "gdebug")
public class DebugCommand extends Registerable {

    private DataRepository dataRepository;

    @Override
    public void register() {
        super.register();
    }

    public DebugCommand(MLib lib, DataRepository dataRepository) {
        super(lib);
        this.dataRepository = dataRepository;
    }

    @SubcommandInfo(subcommand = "save", permission = "gtime.debug.save")
    public void handleSave(Player player, String map, String time) {
        dataRepository.saveTime(player.getUniqueId(), new Time(map, Long.parseLong(time)));
        player.sendMessage("Saved time for map " + map + "!");
    }

    @SubcommandInfo(subcommand = "get", permission = "gtime.debug.get")
    public void handleGet(Player player, String map) {
        Time time = dataRepository.getTime(player.getUniqueId(), map);
        player.sendMessage("Time for map " + map + ": " + time);
    }
}
