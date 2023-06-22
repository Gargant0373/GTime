package me.gargant.containers;

import java.util.ArrayList;
import java.util.Comparator;
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
        for (int i = 0; i <= 45; i++)
            l.add(i);
        return l;
    }

    private long averageTimes = 0;

    @Override
    public List<ItemStack> getOrderableItems(Player player) {
        averageTimes = 0;
        return dataRepository.getAllTimes(player.getUniqueId()).stream().sorted(Comparator.comparing(a -> a.getMap()))
                .map(c -> {
                    int topTime = isTopTime(player, c.getMap());
                    ItemBuilder builder = new ItemBuilder(Material.PAPER);
                    builder.name("&6" + c.getMap()).lore("", "&f&lTIME", "&6" + c.toString(), "", "&f&lACHIEVED",
                            "&6" + c.getLogTimeString());

                    if (topTime != -1) {
                        builder.item(Material.MAP).lore("", "&f&lTOP TIME", "&6#" + (topTime + 1));
                    }

                    averageTimes += c.getTime();
                    return builder.build(lib);
                }).collect(Collectors.toList());
    }

    private int isTopTime(Player player, String map) {
        return dataRepository.getTopTimes(map).stream().map(c -> c.getUuid()).collect(Collectors.toList())
                .indexOf(player.getUniqueId());
    }

    @Override
    public Inventory applyGUIElements(Player player, Inventory inventory, int currentPage, int maxPage) {
        if (currentPage > 1)
            inventory.setItem(getPreviousPageItemPosition(player), getPreviousPageItem(player, currentPage, maxPage));
        if (currentPage < maxPage)
            inventory.setItem(getNextPageItemPosition(player), getNextPageItem(player, currentPage, maxPage));

        inventory.setItem(49, new ItemBuilder(Material.BOOK).name("&fAverage Time").lore("",
                "&6" + new Time("t", averageTimes / getOrderableItems(player).size()).toString()).build(lib));
        return inventory;
    }

    @Override
    public int getPreviousPageItemPosition(Player player) {
        return 48;
    }

    @Override
    public int getNextPageItemPosition(Player player) {
        return 50;
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

    @Override
    public ItemStack getNextPageItem(Player player, int currentPage, int maxPage) {
        return new ItemBuilder(Material.ARROW).name("&6" + currentPage + " &7/ &6" + maxPage)
                .lore("", "&7Next page.").build(lib);
    }

    @Override
    public ItemStack getPreviousPageItem(Player player, int currentPage, int maxPage) {
        return new ItemBuilder(Material.ARROW).name("&6" + currentPage + " &7/ &6" + maxPage)
                .lore("", "&7Previous page.").build(lib);
    }

}
