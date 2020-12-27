package br.com.eterniaserver.eterniacrates.handlers;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.core.APIServer;
import br.com.eterniaserver.eterniacrates.enums.Messages;
import br.com.eterniaserver.eterniacrates.objects.Crate;
import br.com.eterniaserver.eterniacrates.objects.CrateData;
import br.com.eterniaserver.eterniacrates.objects.User;
import br.com.eterniaserver.eternialib.NBTItem;
import br.com.eterniaserver.eternialib.SQL;
import br.com.eterniaserver.eternialib.sql.queries.Insert;
import br.com.eterniaserver.eternialib.sql.queries.Update;

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


    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_AIR) || event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();
        Crate crate = new Crate(block);

        if (action.equals(Action.LEFT_CLICK_BLOCK) && crate.isChest() && crate.isCrate()) {
            crate.getCrate().displayItens(new User(event.getPlayer()));
            event.setCancelled(true);
        }

        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            User user = new User(event.getPlayer());
            ItemStack userItem = user.getPlayer().getInventory().getItemInMainHand();
            
            if (!crate.isChest()) {
                if (userItem.getType() != Material.AIR) {
                    event.setCancelled(new NBTItem(userItem).hasKey(Constants.ETERNIA_KEY));
                }
                return;
            }

            if (crate.isCrate()) {
                event.setCancelled(true);
                isCrate(user, crate.getCrate(), block.getLocation());
            }

            if (!APIServer.hasCachedLoc(user.getUUID())) return;
            
            crate.setCrate(APIServer.getCachedLoc(user.getUUID()));
            String saveloc = block.getX() + ":" + block.getY() + ":" + block.getZ();

            Update update = new Update(EterniaCrates.configs.tableCrates);
            update.set.set(Constants.LOCATION, saveloc);
            update.where.set(Constants.CRATE, APIServer.getCachedLoc(user.getUUID()));
            SQL.executeAsync(update);

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

        ItemStack userItem = player.getInventory().getItemInMainHand();
        NBTItem userKey = new NBTItem(userItem);
        Integer userNBT = userKey.getInteger(Constants.ETERNIA_KEY);

        NBTItem crateKey = new NBTItem(crateData.getKey());
        Integer crateUser = crateKey.getInteger(Constants.ETERNIA_KEY);

        if (userNBT == null || userNBT.intValue() != crateUser.intValue()) {
            user.sendMessage(Messages.ITEM_NO_KEY);
            return;
        }

        if (userItem.getAmount() > 1) {
            player.getInventory().remove(userItem);
            userItem.setAmount(userItem.getAmount() - 1);
            player.getInventory().setItemInMainHand(userItem);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }

        if (APIServer.hasUserCooldown(UUIDMoreCrateName)) {
            Update update = new Update(EterniaCrates.configs.tableUsersCooldown);
            update.set.set(Constants.COOLDOWN, System.currentTimeMillis());
            update.where.set(Constants.UUID, UUIDMoreCrateName);
            SQL.executeAsync(update);
        } else {
            Insert insert = new Insert(EterniaCrates.configs.tableUsersCooldown);
            insert.columns.set(Constants.UUID, Constants.COOLDOWN);
            insert.values.set(UUIDMoreCrateName, System.currentTimeMillis());
            SQL.executeAsync(insert);
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

    private Location getCenter(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX() + 0.5D, loc.getBlockY() + 0.5D, loc.getBlockZ() + 0.5D); 
    }

}
