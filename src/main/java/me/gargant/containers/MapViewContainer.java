package me.gargant.containers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import masecla.mlib.classes.builders.ItemBuilder;
import masecla.mlib.containers.generic.PagedContainer;
import masecla.mlib.main.MLib;
import me.gargant.classes.Time;
import me.gargant.data.DataRepository;
import net.md_5.bungee.api.ChatColor;

public class MapViewContainer extends PagedContainer {

    private DataRepository dataRepository;

    public MapViewContainer(MLib lib, DataRepository dataRepository) {
        super(lib);
        this.dataRepository = dataRepository;
    }

    @Override
    public List<Integer> getUsableSlots(Player player) {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < 45; i++)
            l.add(i);
        return l;
    }

    @Override
    public List<ItemStack> getOrderableItems(Player player) {
        List<Time> times = dataRepository.getAllTimes(player.getUniqueId());
        Collections.sort(times, (a, b) -> a.getMap().compareTo(b.getMap()));
        return times.stream().map(c -> {
            ItemBuilder builder = new ItemBuilder(Material.MAP);
            builder.name("&e" + c.getMap()).lore("", "&f&lTIME", "&e" + c.toString(), "", "&f&lACHIEVED",
                    "&e" + c.getLogTimeString());
            return builder.build(lib);
        }).collect(Collectors.toList());
    }

    @Override
    public Inventory applyGUIElements(Player player, Inventory inventory, int currentPage, int maxPage) {
        return inventory;
    }

    @Override
    public int getPreviousPageItemPosition(Player player) {
        return 52;
    }

    @Override
    public int getNextPageItemPosition(Player player) {
        return 53;
    }

    @Override
    public void usableClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public int getSize(Player player) {
        return 54;
    }

    @Override
    public int getUpdatingInterval() {
        return 0;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.translateAlternateColorCodes('&', "&fTIMES &7/ &e" + player.getName());
    }

    @Override
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

}
