package br.com.eterniaserver.eterniacrates.generics;

import br.com.eterniaserver.eterniacrates.objects.CratesData;
import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.UUIDFetcher;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class EventPlayerInteract implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        final Action action = e.getAction();
        final Player player = e.getPlayer();

        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            final Block block = e.getClickedBlock();
            if (block.getType().equals(Material.CHEST)) {
                final UUID uuid = UUIDFetcher.getUUIDOf(player.getName());
                if (PluginVars.cacheSetLoc.containsKey(uuid)) {
                    final String cratesName = PluginVars.cacheSetLoc.get(uuid);
                    PluginVars.cacheSetLoc.remove(uuid);
                    final CratesData cratesData = PluginVars.cratesNameMap.get(cratesName);
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
                        isCrate(saveloc, player);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    private void isCrate(String location, Player player) {
        float max = 99.9f;
        float min = 0.00f;
        double random = min + Math.random() * (max - min);
        final CratesData cratesData = PluginVars.cratesDataMap.get(location);
        final String UUIDMoreCrateName = UUIDFetcher.getUUIDOf(player.getName()) + "." + cratesData.getCratesName();

        if (hasCooldown(PluginVars.usersCooldown.getOrDefault(UUIDMoreCrateName, 0L), cratesData.getCooldown())) {
            if (player.getInventory().getItemInMainHand().equals(cratesData.getKey())) {
                if (PluginVars.usersCooldown.containsKey(UUIDMoreCrateName)) {
                    EQueries.executeQuery(PluginConstants.getQueryUpdate(PluginConfigs.TABLE_USERS,  "cooldown", System.currentTimeMillis(), "uuid", UUIDMoreCrateName));
                } else {
                    EQueries.executeQuery(PluginConstants.getQueryInsert(PluginConfigs.TABLE_USERS, "(uuid, cooldown)", "('" + UUIDMoreCrateName + "', '" + System.currentTimeMillis() + "')"));
                }
                PluginVars.usersCooldown.put(UUIDMoreCrateName, System.currentTimeMillis());
                ItemStack itemStack = null;
                AtomicReference<Float> lowestNumberAboveRandom = new AtomicReference<>(100.0f);
                Map<Float, ItemStack> itens = cratesData.getItens();
                itens.forEach((k, v) -> {
                    if (k < lowestNumberAboveRandom.get() && k > random) {
                        lowestNumberAboveRandom.set(k);
                    }
                });
                if (lowestNumberAboveRandom.get() < 100.0f) {
                    itemStack = itens.get(lowestNumberAboveRandom.get());
                }

                if (itemStack != null) {
                    giveItem(itemStack, player);
                    return;
                }

                player.sendMessage(PluginMSGs.ITEM_FAIL);
            } else {
                player.sendMessage(PluginMSGs.NO_KEY);
            }
        } else {
            player.sendMessage(PluginMSGs.COOLDOWN);
        }
    }

    private void giveItem(ItemStack itemStack, Player player) {
        player.getInventory().addItem(itemStack);
        player.sendMessage(PluginMSGs.ITEM_WINNER.replace("%item%", itemStack.getItemMeta().getDisplayName()));
    }

    private boolean hasCooldown(long cooldown, int timeNeeded) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cooldown) >= timeNeeded;
    }

}
