package me.gargant;

import org.bukkit.plugin.java.JavaPlugin;

import masecla.mlib.main.MLib;
import me.gargant.classes.GTimeAPI;
import me.gargant.commands.DebugCommand;
import me.gargant.commands.GTimeCommand;
import me.gargant.containers.MapViewContainer;
import me.gargant.data.DataRepository;
import me.gargant.data.MojangApiRepository;
import me.gargant.data.SQLRepository;
import me.gargant.data.YMLRepository;
import me.gargant.placeholders.CurrentTimePlaceholder;
import me.gargant.placeholders.MapPlaceholder;
import me.gargant.services.RunService;

public class GTime extends JavaPlugin {

    private MLib lib;

    private DataRepository dataRepository;
    private RunService runService;
    private MojangApiRepository mojangApiRepository;

    private static GTimeAPI api;

    @Override
    public void onEnable() {
        this.lib = new MLib(this);
        this.lib.getConfigurationAPI().requireAll();

        this.registerRepository();
        this.runService = new RunService(lib, dataRepository);
        this.runService.register();

        this.mojangApiRepository = new MojangApiRepository();

        new DebugCommand(lib, dataRepository).register();
        new GTimeCommand(lib, runService, dataRepository, mojangApiRepository).register();

        new MapViewContainer(lib, dataRepository).register();

        api = new GTimeAPI(dataRepository, runService);

        new CurrentTimePlaceholder(lib, runService).register();
        new MapPlaceholder(lib, dataRepository).register();

        this.disableAntispam();
    }

    public static GTimeAPI api() {
        return api;
    }

    private void disableAntispam() {
        lib.getMessagesAPI().setAntispamDelay("times.personal-best", 0);
        lib.getMessagesAPI().setAntispamDelay("times.finished", 0);
        lib.getMessagesAPI().setAntispamDelay("leaderboard.message.header", 0);
        lib.getMessagesAPI().setAntispamDelay("leaderboard.message.body", 0);
        lib.getMessagesAPI().setAntispamDelay("leaderboard.message.footer", 0);
    }

    @Override
    public void onDisable() {
        this.dataRepository.closeConnection();
    }

    private void registerRepository() {
        String type = lib.getConfigurationAPI().getConfig().getString("database", "null");
        switch (type) {
            case "sql":
                this.dataRepository = new SQLRepository(lib);
                lib.getLoggerAPI().information("Using SQL database.");
                break;
            case "yml":
                this.dataRepository = new YMLRepository(lib);
                lib.getLoggerAPI().information("Using YML database.");
                break;
            default:
                lib.getLoggerAPI().error("Invalid data type: " + type);
                lib.getLoggerAPI().error("Valid types are: sql, yml");
                lib.getLoggerAPI().error("Disabling plugin.");
                this.setEnabled(false);
                return;
        }
        this.dataRepository.initialize();
    }
}
