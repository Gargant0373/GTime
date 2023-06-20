package me.gargant.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MojangApiRepository {

    private Map<UUID, String> nameCache = new HashMap<>();
    private Map<UUID, Long> nameCacheTimes = new HashMap<>();

    private String API_URL = "https://api.minetools.eu/uuid";

    public String getPlayerName(UUID uuid) {
        if (nameCache.containsKey(uuid) && nameCacheTimes.get(uuid) > System.currentTimeMillis() - 1000 * 60 * 60 * 24)
            return nameCache.get(uuid);
        String name = fetchName(uuid);
        if (name == null)
            return null;
        nameCache.put(uuid, name);
        nameCacheTimes.put(uuid, System.currentTimeMillis());
        return name;
    }

    private String fetchName(UUID uuid) {
        if (uuid == null)
            return null;
        if (Bukkit.getPlayer(uuid) != null)
            return Bukkit.getPlayer(uuid).getName();

        String url = API_URL + "/" + uuid.toString().replace("-", "");

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new URL(url).openStream()));

            JsonObject obj = JsonParser.parseReader(in).getAsJsonObject();

            in.close();
            return obj.get("name").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
