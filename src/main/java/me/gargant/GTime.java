package me.gargant;

import org.bukkit.plugin.java.JavaPlugin;

import masecla.mlib.main.MLib;
import me.gargant.commands.DebugCommand;
import me.gargant.commands.GTimeCommand;
import me.gargant.data.DataRepository;
import me.gargant.data.SQLRepository;
import me.gargant.data.YMLRepository;
import me.gargant.services.RunService;

public class GTime extends JavaPlugin {
    
    private MLib lib;

    private DataRepository dataRepository;
    private RunService runService;

    @Override
    public void onEnable() {
        this.lib = new MLib(this);
        this.lib.getConfigurationAPI().requireAll();

        this.registerRepository();
        this.runService = new RunService(lib, dataRepository);

        new DebugCommand(lib, dataRepository).register();
        new GTimeCommand(lib, runService).register();
    }

    @Override
    public void onDisable() {
        this.dataRepository.closeConnection();
    }

    private void registerRepository() {
        String type = lib.getConfigurationAPI().getConfig().getString("database", "null");
        switch(type) {
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
                break;
        }
    }
}
