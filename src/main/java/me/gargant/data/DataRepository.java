package me.gargant.data;

import java.util.List;
import java.util.UUID;

import me.gargant.classes.Time;

public interface DataRepository {
    
    /**
     * Initializes the repository. This method should be called before any other method.
     * Plugin handles initialization and database connection closing itself.
     */
    public void initialize();
    /**
     * Closes the connection to the repository. This method should be called when the plugin is disabled.
     */
    public void closeConnection();
    /**
     * Saves a time to the repository.
     * @param uuid the {@link UUID} of the player.
     * @param time the time to save.
     */
    public void saveTime(UUID uuid, Time time);
    /**
     * Gets a time from the repository.
     * @param uuid the {@link UUID} of the player.
     * @param map the map to get the time from.
     * @return the {@link Time} on the map or null.
     */
    public Time getTime(UUID uuid, String map);
    public List<Time> getAllTimes(UUID uuid);

}
