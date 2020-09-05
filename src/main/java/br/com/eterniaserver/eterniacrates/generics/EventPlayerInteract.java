package br.com.eterniaserver.eterniacrates.generics;

import br.com.eterniaserver.eterniacrates.objects.CratesData;
import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.NBTItem;
import br.com.eterniaserver.eternialib.UUIDFetcher;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EventPlayerInteract implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        final Action action = e.getAction();
        final Player player = e.getPlayer();
        final ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
            final Block block = e.getClickedBlock();
            if (block.getType().equals(Material.CHEST)) {
                Location loc = block.getLocation();
                final String saveloc = loc.getWorld().getName() + ":" + ((int) loc.getX()) + ":" + ((int) loc.getY()) +
                        ":" + ((int) loc.getZ()) + ":" + 0 + ":" + 0;
                if (PluginVars.cratesDataMap.containsKey(saveloc)) {
                    final CratesData cratesData = PluginVars.cratesDataMap.get(saveloc);
                    player.sendMessage(PluginMSGs.LIST_TITLE.replace("%crate%", cratesData.getCratesName()));
                    for (int i = 0; i < cratesData.itensChance.size(); i++) {
                        ItemStack v = cratesData.itensId.get(i);
                        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, Bukkit.getItemFactory().hoverContentOf(v));
                        String name = "";
                        double k = cratesData.itensChance.get(i);
                        if (v.getItemMeta() != null) {
                            name = v.getItemMeta().getDisplayName();
                            if (name.equals("")) {
                                name = v.getI18NDisplayName();
                            }
                        }
                        TextComponent component = new TextComponent(PluginMSGs.LIST_ITENS.replace("%id%", String.valueOf(i)).replace("%item%", "x" + v.getAmount() + " " + name).replace("%chance%", (k * 100) + "%"));
                        component.setHoverEvent(event);
                        player.sendMessage(component);
                    }
                    e.setCancelled(true);
                }
            }
        }

        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            final Block block = e.getClickedBlock();
            if (block.getType().equals(Material.CHEST)) {
                final UUID uuid = UUIDFetcher.getUUIDOf(player.getName());
                if (PluginVars.cacheSetLoc.containsKey(uuid)) {
                    final String cratesName = PluginVars.cacheSetLoc.get(uuid);
                    PluginVars.cacheSetLoc.remove(uuid);
                    final CratesData cratesData = PluginVars.cratesNameMap.get(cratesName);
                    if (cratesData.getCratesLocation() != null) {
                        PluginVars.cratesDataMap.remove(cratesData.getCratesLocation());
                    }
                    Location loc = block.getLocation();
                    final String saveloc = loc.getWorld().getName() + ":" + ((int) loc.getX()) + ":" + ((int) loc.getY()) +
                            ":" + ((int) loc.getZ()) + ":" + 0 + ":" + 0;
                    cratesData.setCratesLocation(saveloc);
                    PluginVars.cratesDataMap.put(saveloc, cratesData);
                    PluginVars.cratesNameMap.put(cratesName, cratesData);
                    EQueries.executeQuery(PluginConstants.getQueryUpdate(PluginConfigs.TABLE_CRATES, "location", saveloc, "crate", cratesName));
                    player.sendMessage(PluginMSGs.LOC_SETED);
                    e.setCancelled(true);
                } else {
                    Location loc = block.getLocation();
                    final String saveloc = loc.getWorld().getName() + ":" + ((int) loc.getX()) + ":" + ((int) loc.getY()) +
                            ":" + ((int) loc.getZ()) + ":" + 0 + ":" + 0;
                    if (PluginVars.cratesDataMap.containsKey(saveloc)) {
                        isCrate(saveloc, player, block.getLocation());
                        e.setCancelled(true);
                    }
                }
            } else if (itemStack.getType() != Material.AIR && itemStack.getItemMeta().getDisplayName().contains("Chave")) {
                e.setCancelled(true);
            }
        }
    }

    private void isCrate(String location, Player player, Location locationC) {
        final CratesData cratesData = PluginVars.cratesDataMap.get(location);
        final String UUIDMoreCrateName = UUIDFetcher.getUUIDOf(player.getName()) + "." + cratesData.getCratesName();

        if (hasCooldown(PluginVars.usersCooldown.getOrDefault(UUIDMoreCrateName, 0L), cratesData.getCooldown())) {
            ItemStack key = player.getInventory().getItemInMainHand();
            int amount = key.getAmount();
            key.setAmount(1);
            if (key.equals(cratesData.getKey())) {
                if (amount > 1) {
                    player.getInventory().remove(key);
                    key.setAmount(amount - 1);
                    player.getInventory().setItemInMainHand(key);
                } else {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }
                if (PluginVars.usersCooldown.containsKey(UUIDMoreCrateName)) {
                    EQueries.executeQuery(PluginConstants.getQueryUpdate(PluginConfigs.TABLE_USERS,  "cooldown", System.currentTimeMillis(), "uuid", UUIDMoreCrateName));
                } else {
                    EQueries.executeQuery(PluginConstants.getQueryInsert(PluginConfigs.TABLE_USERS, "(uuid, cooldown)", "('" + UUIDMoreCrateName + "', '" + System.currentTimeMillis() + "')"));
                }
                PluginVars.usersCooldown.put(UUIDMoreCrateName, System.currentTimeMillis());
                ItemStack itemStack = null;
                double lowestNumberAboveRandom = 1.1;
                int id = 0;
                for (int i = 0; i < cratesData.itensChance.size(); i++) {
                    if (cratesData.itensChance.get(i) < lowestNumberAboveRandom && cratesData.itensChance.get(i) > Math.random()) {
                        lowestNumberAboveRandom = cratesData.itensChance.get(i);
                        id = i;
                    }
                }

                if (lowestNumberAboveRandom < 1.00001) {
                    itemStack = cratesData.itensId.get(id);
                }

                if (itemStack != null) {
                    giveItem(itemStack, player, locationC);
                    return;
                }

                locationC = getCenter(locationC);
                for (double angle = 0; angle < 2 * Math.PI; angle += 0.2) {
                    final double x = 2 * Math.cos(angle);
                    final double z = 2 * Math.sin(angle);
                    locationC.add(x, 1, z);
                    locationC.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, locationC, 1);
                    locationC.getWorld().playSound(locationC, Sound.AMBIENT_UNDERWATER_EXIT, 1f, 1f);
                    locationC.subtract(x, 1, z);
                }
                player.sendMessage(PluginMSGs.ITEM_FAIL);
            } else {
                player.sendMessage(PluginMSGs.NO_KEY);
            }
        } else {
            player.sendMessage(PluginMSGs.COOLDOWN);
        }
    }

    private void giveItem(ItemStack itemStack, Player player, Location location) {
        location = getCenter(location);
        for (double angle = 0; angle < 2 * Math.PI; angle += 0.2) {
            final double x = 2 * Math.cos(angle);
            final double z = 2 * Math.sin(angle);
            location.add(x, 1, z);
            location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 1);
            location.getWorld().playSound(location, Sound.AMBIENT_UNDERWATER_EXIT, 1f, 1f);
            location.subtract(x, 1, z);
        }
        player.getInventory().addItem(new ItemStack(itemStack));
        String name = itemStack.getItemMeta().getDisplayName();
        if (name == null || name.equals("")) {
            name = itemStack.getI18NDisplayName();
        }
        player.sendMessage(PluginMSGs.ITEM_WINNER.replace("%item%", "x" + itemStack.getAmount() + " " + name));
    }

    private boolean hasCooldown(long cooldown, int timeNeeded) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cooldown) >= timeNeeded;
    }

    public Location getCenter(Location loc) {
        return new Location(loc.getWorld(),
                getRelativeCoord(loc.getBlockX()),
                getRelativeCoord(loc.getBlockY()),
                getRelativeCoord(loc.getBlockZ()));
    }

    private double getRelativeCoord(int i) {
        double d = i;
        d = d < 0 ? d - .5 : d + .5;
        return d;
    }

}
