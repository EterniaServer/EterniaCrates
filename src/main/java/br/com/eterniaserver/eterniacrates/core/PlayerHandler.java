package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.core.enums.Messages;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.MessageOptions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.TimeUnit;

public class PlayerHandler implements Listener {

    private final EterniaCrates plugin;

    public PlayerHandler(EterniaCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.PHYSICAL) || action.equals(Action.RIGHT_CLICK_AIR) || block == null) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack playerItem = player.getInventory().getItemInMainHand();

        if (block.getState() instanceof Chest chest) {
            PersistentDataContainer dataContainer = chest.getPersistentDataContainer();
            String crateName = dataContainer.getOrDefault(plugin.getCrateKey(), PersistentDataType.STRING, "");

            if (crateName.isEmpty() || !EterniaCrates.getCrateAPI().existsCrate(crateName)) {
                if (playerItem.getType() != Material.AIR) {
                    PersistentDataContainer itemContainer = playerItem.getItemMeta().getPersistentDataContainer();
                    event.setCancelled(itemContainer.has(plugin.getCrateKey()));
                } else if (EterniaCrates.getCrateAPI().hasCachedLoc(player.getUniqueId())) {
                    crateName = EterniaCrates.getCrateAPI().getCachedLoc(player.getUniqueId());
                    dataContainer.set(plugin.getCrateKey(), PersistentDataType.STRING, crateName);

                    EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_LOC_SET);
                    event.setCancelled(true);
                }
                return;
            }

            CrateDTO crateDTO = EterniaCrates.getCrateAPI().read(crateName);

            if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                crateDTO.displayMessage(plugin, player);
            } else {
                openKey(player, crateDTO, chest.getLocation());
            }

            event.setCancelled(true);
        } else if (playerItem.getType() != Material.AIR) {
            PersistentDataContainer itemContainer = playerItem.getItemMeta().getPersistentDataContainer();
            event.setCancelled(itemContainer.has(plugin.getCrateKey()));
        }
    }

    private void openKey(Player player, CrateDTO crateDTO, Location locationC) {
        UserCooldownDto cooldown = EterniaCrates.getCrateAPI().getUserCooldown(
                player.getUniqueId(),
                crateDTO.getCrateName()
        );

        if (hasCooldown(cooldown.getCooldown(), crateDTO.getCooldown())) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.IN_COOLDOWN);
            return;
        }

        ItemStack userItem = player.getInventory().getItemInMainHand();
        if (userItem.getType() == Material.AIR) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.ITEM_NO_KEY);
            return;
        }

        PersistentDataContainer dataContainer = userItem.getItemMeta().getPersistentDataContainer();
        String keyData = dataContainer.getOrDefault(plugin.getCrateKey(), PersistentDataType.STRING, "");

        if (!keyData.equals(crateDTO.getCrateId())) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.ITEM_NO_KEY);
            return;
        }

        if (userItem.getAmount() > 1) {
            player.getInventory().remove(userItem);
            userItem.setAmount(userItem.getAmount() - 1);
            player.getInventory().setItemInMainHand(userItem);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }

        EterniaCrates.getCrateAPI().updateUserCooldown(cooldown);

        double randomValue = Math.random();

        CrateItemDTO selectedItem = null;
        for (CrateItemDTO crateItemDTO : crateDTO.getItems()) {
            if (crateItemDTO.getChance() > randomValue) {
                selectedItem = crateItemDTO;
            }
        }

        if (selectedItem != null) {
            giveItem(selectedItem.getItem(), player, locationC);
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
        EterniaLib.getChatCommons().sendMessage(player, Messages.ITEM_FAIL);
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

        Component display = itemStack.getItemMeta().displayName();
        String displayStr = display != null
                ? PlainTextComponentSerializer.plainText().serialize(display)
                : itemStack.getType().toString();

        MessageOptions options = new MessageOptions("x" + itemStack.getAmount() + " " + displayStr);
        EterniaLib.getChatCommons().sendMessage(player, Messages.ITEM_WINNER, options);
    }

    private boolean hasCooldown(long cooldown, int timeNeeded) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cooldown) < timeNeeded;
    }

    private Location getCenter(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX() + 0.5D, loc.getBlockY() + 0.5D, loc.getBlockZ() + 0.5D); 
    }

}
