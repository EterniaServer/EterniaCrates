package br.com.eterniaserver.eterniacrates.events;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.core.APIServer;
import br.com.eterniaserver.eterniacrates.enums.Messages;
import br.com.eterniaserver.eterniacrates.objects.Crate;
import br.com.eterniaserver.eterniacrates.objects.CrateData;
import br.com.eterniaserver.eterniacrates.objects.User;

import br.com.eterniaserver.eternialib.EQueries;
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

import java.util.concurrent.TimeUnit;

public class PlayerHandler implements Listener {

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_AIR)) return;
        if (event.getClickedBlock() == null) return;

        User user = new User(event.getPlayer());

        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
            Crate crate = new Crate(event.getClickedBlock());
            if (crate.isChest() && crate.isCrate()) {
                crate.getCrate().displayItens(user);
                event.setCancelled(true);
            }
        }

        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            Crate crate = new Crate(event.getClickedBlock());
            if (!crate.isChest()) return;
            if (crate.isCrate()) {
                isCrate(user, crate.getCrate(), block.getLocation());
                event.setCancelled(true);
            }
            if (!APIServer.hasCachedLoc(user.getUUID())) return;
            crate.setCrate(APIServer.getCachedLoc(user.getUUID()));
            String saveloc = block.getX() + ":" + block.getY() + ":" + block.getZ();

            EQueries.executeQuery(Constants.getQueryUpdate(EterniaCrates.configs.tableCrates, "location", saveloc, "crate", APIServer.getCachedLoc(user.getUUID())));
            APIServer.removeCachedLoc(user.getUUID());
            user.sendMessage(Messages.CRATE_LOC_SET);
            event.setCancelled(true);
        }
    }

    private void isCrate(User user, CrateData crateData, Location locationC) {
        final String UUIDMoreCrateName = user.getUUID() + "." + crateData.getCratesName();
        if (hasCooldown(APIServer.getUserCooldown(UUIDMoreCrateName), crateData.getCooldown())) {
            user.sendMessage(Messages.IN_COOLDOWN);
            return;
        }

        Player player = user.getPlayer();

        ItemStack key = player.getInventory().getItemInMainHand();
        int amount = key.getAmount();
        key.setAmount(1);
        if (key.equals(crateData.getKey())) {
            if (amount > 1) {
                player.getInventory().remove(key);
                key.setAmount(amount - 1);
                player.getInventory().setItemInMainHand(key);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            if (APIServer.hasUserCooldown(UUIDMoreCrateName)) {
                EQueries.executeQuery(Constants.getQueryUpdate(EterniaCrates.configs.tableUsersCooldown,  "cooldown", System.currentTimeMillis(), "uuid", UUIDMoreCrateName));
            } else {
                EQueries.executeQuery(Constants.getQueryInsert(EterniaCrates.configs.tableUsersCooldown, "(uuid, cooldown)", "('" + UUIDMoreCrateName + "', '" + System.currentTimeMillis() + "')"));
            }
            APIServer.putUserCooldown(UUIDMoreCrateName, System.currentTimeMillis());
            ItemStack itemStack = null;
            double lowestNumberAboveRandom = 1.1;
            int id = 0;
            for (int i = 0; i < crateData.itensChance.size(); i++) {
                if (crateData.itensChance.get(i) < lowestNumberAboveRandom && crateData.itensChance.get(i) > Math.random()) {
                    lowestNumberAboveRandom = crateData.itensChance.get(i);
                    id = i;
                }
            }

            if (lowestNumberAboveRandom < 1.00001) {
                itemStack = crateData.itensId.get(id);
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
            user.sendMessage(Messages.ITEM_FAIL);
        } else {
            user.sendMessage(Messages.ITEM_NO_KEY);
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
        player.sendMessage(EterniaCrates.msg.getMessage(Messages.ITEM_WINNER, true, "x" + itemStack.getAmount() + " " + name));
    }

    private boolean hasCooldown(long cooldown, int timeNeeded) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cooldown) < timeNeeded;
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
