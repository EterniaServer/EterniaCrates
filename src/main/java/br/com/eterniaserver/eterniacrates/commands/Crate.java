package br.com.eterniaserver.eterniacrates.commands;

import br.com.eterniaserver.acf.BaseCommand;
import br.com.eterniaserver.acf.CommandHelp;
import br.com.eterniaserver.acf.annotation.CatchUnknown;
import br.com.eterniaserver.acf.annotation.CommandAlias;
import br.com.eterniaserver.acf.annotation.CommandCompletion;
import br.com.eterniaserver.acf.annotation.CommandPermission;
import br.com.eterniaserver.acf.annotation.Conditions;
import br.com.eterniaserver.acf.annotation.Default;
import br.com.eterniaserver.acf.annotation.Description;
import br.com.eterniaserver.acf.annotation.HelpCommand;
import br.com.eterniaserver.acf.annotation.Subcommand;
import br.com.eterniaserver.acf.annotation.Syntax;
import br.com.eterniaserver.acf.bukkit.contexts.OnlinePlayer;
import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.core.APIServer;
import br.com.eterniaserver.eterniacrates.enums.Messages;
import br.com.eterniaserver.eterniacrates.objects.CrateData;
import br.com.eterniaserver.eterniacrates.objects.User;
import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.NBTItem;
import br.com.eterniaserver.eternialib.sql.Connections;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@CommandAlias("%crate")
public class Crate extends BaseCommand {

    public Crate() {
        Map<String, String> temp = EQueries.getMapString(Constants.getQuerySelectAll(EterniaCrates.configs.tableUsersCooldown), "uuid", "cooldown");
        temp.forEach((k, v) -> APIServer.putUserCooldown(k, Long.parseLong(v)));

        if (EterniaLib.getMySQL()) {
            EterniaLib.getConnections().executeSQLQuery(connection -> {
                final PreparedStatement getHashMap = connection.prepareStatement(Constants.getQuerySelectAll(EterniaCrates.configs.tableCrates));
                final ResultSet resultSet = getHashMap.executeQuery();
                getCrates(resultSet);
                getHashMap.close();
                resultSet.close();
            });
            EterniaLib.getConnections().executeSQLQuery(connection -> {
                final PreparedStatement getHashMap = connection.prepareStatement(Constants.getQuerySelectAll(EterniaCrates.configs.tableItens));
                final ResultSet resultSet = getHashMap.executeQuery();
                getItens(resultSet);
                getHashMap.close();
                resultSet.close();
            });
        } else {
            try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement(Constants.getQuerySelectAll(EterniaCrates.configs.tableCrates)); ResultSet resultSet = getHashMap.executeQuery()) {
                getCrates(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement(Constants.getQuerySelectAll(EterniaCrates.configs.tableItens)); ResultSet resultSet = getHashMap.executeQuery()) {
                getItens(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    private void getItens(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            final String cratesName = resultSet.getString("crate");
            final CrateData crateData = APIServer.getCrate(cratesName);
            final byte[] bytes = resultSet.getBytes("item");
            final double chance = resultSet.getDouble("chance");
            crateData.addItens(chance, ItemStack.deserializeBytes(bytes));
        }
    }

    private void getCrates(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            final String cratesName = resultSet.getString("crate");
            final CrateData cratesData = new CrateData(cratesName);
            cratesData.setCooldown(resultSet.getInt("cooldown"));
            final byte[] key = resultSet.getBytes("cratekey");
            if (key != null) {
                cratesData.setKey(ItemStack.deserializeBytes(key));
            }
            final String locString = resultSet.getString("location");
            if (locString != null) {
                cratesData.setCratesLocation(locString);
            }
            APIServer.putCrate(cratesName, cratesData);
        }
    }

    @Default
    @Description("%crate_description")
    @Syntax("%crate_syntax")
    @CommandPermission("%crate_perm")
    @CatchUnknown
    @HelpCommand
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("%crate_key")
    @Syntax("%crate_key_syntax")
    @CommandCompletion("@players caixa 1")
    @Description("%crate_description")
    @CommandPermission("%crate_perm")
    public void onGiveKey(CommandSender sender, OnlinePlayer onlinePlayer, String cratesName, Integer amount) {
        Player player = onlinePlayer.getPlayer();
        cratesName = cratesName.toLowerCase();
        if (!APIServer.existsCrate(cratesName)) {
            sender.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_NOT_FOUND, true, cratesName));
            return;
        }

        ItemStack itemStack = new ItemStack(APIServer.getCrate(cratesName).getKey());
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        player.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_KEY_RECEIVED, true, String.valueOf(amount), cratesName));
        sender.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_KEY_SEND, true, String.valueOf(amount), cratesName, player.getName(), player.getDisplayName()));
    }

    @Subcommand("%crate_create")
    @Syntax("%crate_create_syntax")
    @CommandPermission("%crate_create_perm")
    @Description("%crate_create_description")
    public void onCrateCreate(CommandSender player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        if (APIServer.existsCrate(cratesName)) {
            player.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_ALREADY_EXIST, true, cratesName));
            return;
        }

        APIServer.createCrate(cratesName);
        EQueries.executeQuery(Constants.getQueryInsert(EterniaCrates.configs.tableCrates, "(crate)", "('" + cratesName + "')"));
        player.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_CREATED, true, cratesName));
    }

    @Subcommand("%crate_cooldown")
    @Syntax("%crate_cooldown_syntax")
    @CommandPermission("%crate_cooldown_perm")
    @Description("%crate_cooldown_description")
    public void onCrateCooldown(CommandSender player, String cratesName, Integer cooldown) {
        cratesName = cratesName.toLowerCase();
        if (!APIServer.existsCrate(cratesName)) {
            player.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_NOT_FOUND, true, cratesName));
            return;
        }

        CrateData cratesData = APIServer.getCrate(cratesName);
        cratesData.setCooldown(cooldown);
        EQueries.executeQuery(Constants.getQueryUpdate(EterniaCrates.configs.tableCrates, "cooldown", cooldown, "crate", cratesName));
        player.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_SET_COOLDOWN, true, cratesName, String.valueOf(cooldown)));
    }

    @Subcommand("%crate_location")
    @Syntax("%crate_location_syntax")
    @CommandPermission("%crate_location_perm")
    @Description("%crate_location_description")
    public void onCrateLocation(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        User user = new User(player);
        if (!APIServer.existsCrate(cratesName)) {
            user.sendMessage(Messages.CRATE_NOT_FOUND, cratesName);
            return;
        }

        APIServer.putCachedLoc(user.getUUID(), cratesName);
        user.sendMessage(Messages.CRATE_LOCATION, cratesName);
    }

    @Subcommand("%crate_putitem")
    @Syntax("%crate_putitem_syntax")
    @CommandPermission("%crate_putitem_perm")
    @Description("%crate_putitem_description")
    public void onCrateAddItem(Player player, String cratesName, @Conditions("limits:min=0,max=1") Double chance) {
        cratesName = cratesName.toLowerCase();
        User user = new User(player);
        if (!APIServer.existsCrate(cratesName)) {
            user.sendMessage(Messages.CRATE_NOT_FOUND, cratesName);
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().equals(Material.AIR)) {
            user.sendMessage(Messages.NO_AIR);
            return;
        }

        CrateData crateData = APIServer.getCrate(cratesName);
        crateData.addItens(chance, itemStack);

        if (EterniaLib.getMySQL()) {
            String finalCratesName = cratesName;
            EterniaLib.getConnections().executeSQLQuery(connection -> {
                final PreparedStatement getHashMap = connection.prepareStatement("INSERT INTO " + EterniaCrates.configs.tableItens + " (crate, `item`, chance) VALUES (?, ?, ?)");
                getHashMap.setString(1, finalCratesName);
                getHashMap.setBytes(2, itemStack.serializeAsBytes());
                getHashMap.setDouble(3, chance);
                getHashMap.execute();
                getHashMap.close();
            });
        } else {
            try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement("INSERT INTO " + EterniaCrates.configs.tableItens + " (crate, `item`, chance) VALUES (?, ?, ?)")) {
                getHashMap.setString(1, cratesName);
                getHashMap.setBytes(2, itemStack.serializeAsBytes());
                getHashMap.setDouble(3, chance);
                getHashMap.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        user.sendMessage(Messages.CRATE_ITEM_PUT, cratesName);
    }

    @Subcommand("%crate_listitens")
    @Syntax("%crate_listitens_syntax")
    @CommandPermission("%crate_listitens_perm")
    @Description("crate_listitens_description")
    public void listItens(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        User user = new User(player);
        if (!APIServer.existsCrate(cratesName)) {
            user.sendMessage(Messages.CRATE_NOT_FOUND, cratesName);
            return;
        }

        APIServer.getCrate(cratesName).displayItens(user);
    }

    @Subcommand("%crate_delete")
    @Syntax("%crate_delete_syntax")
    @CommandPermission("%crate_delete_perm")
    @Description("%crate_delete_description")
    public void removeCaixa(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        User user = new User(player);
        if (!APIServer.existsCrate(cratesName)) {
            user.sendMessage(Messages.CRATE_NOT_FOUND, cratesName);
            return;
        }

        APIServer.removeCrate(cratesName);
        EQueries.executeQuery(Constants.getQueryDelete(EterniaCrates.configs.tableItens, "crate", cratesName));
        EQueries.executeQuery(Constants.getQueryDelete(EterniaCrates.configs.tableCrates, "crate", cratesName));
        user.sendMessage(Messages.CRATE_DELETED);
    }

    @Subcommand("%crate_setkey")
    @Syntax("%crate_setkey_syntax")
    @CommandPermission("%crate_setkey_perm")
    @Description("%crate_setkey_description")
    public void onCrateSetKey(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        User user = new User(player);
        if (!APIServer.existsCrate(cratesName)) {
            user.sendMessage(Messages.CRATE_NOT_FOUND, cratesName);
            return;
        }

        ItemStack tempItem = player.getInventory().getItemInMainHand();
        if (tempItem.getType().equals(Material.AIR)) {
            user.sendMessage(Messages.NO_AIR);
            return;
        }

        CrateData cratesData = APIServer.getCrate(cratesName);
        NBTItem item = new NBTItem(tempItem);
        item.setInteger("EterniaKey", cratesName.hashCode());
        ItemStack itemStack = item.getItem();
        player.getInventory().setItemInMainHand(itemStack);
        cratesData.setKey(itemStack);
        if (EterniaLib.getMySQL()) {
            String finalCratesName = cratesName;
            EterniaLib.getConnections().executeSQLQuery(connection -> {
                final PreparedStatement getHashMap = connection.prepareStatement("UPDATE " + EterniaCrates.configs.tableCrates + " SET cratekey=? WHERE crate=?");
                getHashMap.setBytes(1, itemStack.serializeAsBytes());
                getHashMap.setString(2, finalCratesName);
                getHashMap.execute();
                getHashMap.close();
            });
        } else {
            try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement("UPDATE " + EterniaCrates.configs.tableCrates + " SET cratekey=? WHERE crate=?")) {
                getHashMap.setBytes(1, itemStack.serializeAsBytes());
                getHashMap.setString(2, cratesName);
                getHashMap.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        user.sendMessage(Messages.CRATE_KEY_SETTED, cratesName);
    }

    @Subcommand("%crate_nbt")
    @CommandPermission("%crate_nbt_perm")
    @Syntax("%crate_nbt_syntax")
    @Description("%crate_nbt_description")
    public void onGetNBT(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        User user = new User(player);
        if (!APIServer.existsCrate(cratesName)) {
            user.sendMessage(Messages.CRATE_NOT_FOUND, cratesName);
            return;
        }

        user.sendMessage(Messages.KEY_NBT, String.valueOf(cratesName.hashCode()));
    }

    @Subcommand("%crate_givekeyall")
    @Syntax("%crate_givekeyall_syntax")
    @CommandPermission("%crate_givekeyall_perm")
    @Description("%crate_givekeyall_description")
    public void onGiveKeyAll(CommandSender sender, String cratesName, Integer amount) {
        cratesName = cratesName.toLowerCase();
        if (!APIServer.existsCrate(cratesName)) {
            sender.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_NOT_FOUND, true, cratesName));
            return;
        }

        ItemStack itemStack = APIServer.getCrate(cratesName).getKey();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(new ItemStack(itemStack));
            player.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_KEY_RECEIVED, true, String.valueOf(amount), cratesName));
        }
        sender.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_KEY_SEND, true, String.valueOf(amount), cratesName, "todos", "todos"));
    }

    @Subcommand("%crate_removeitem")
    @Syntax("%crate_removeitem_syntax")
    @CommandPermission("%crate_removeitem_perm")
    @Description("%crate_removeitem_description")
    public void removeItem(CommandSender player, String cratesName, Integer id) {
        cratesName = cratesName.toLowerCase();
        if (!APIServer.existsCrate(cratesName)) {
            player.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_NOT_FOUND, true, cratesName));
            return;
        }

        CrateData cratesData = APIServer.getCrate(cratesName);
        if (cratesData.itensId.get(id) == null) {
            player.sendMessage(EterniaCrates.msg.getMessage(Messages.NO_ITEM, true));
            return;
        }

        if (EterniaLib.getMySQL()) {
            String finalCratesName = cratesName;
            EterniaLib.getConnections().executeSQLQuery(connection -> {
                PreparedStatement getHashMap = connection.prepareStatement("DELETE FROM " + EterniaCrates.configs.tableItens + " WHERE crate=? AND item=?");
                getHashMap.setString(1, finalCratesName);
                getHashMap.setBytes(2, cratesData.itensId.get(id).serializeAsBytes());
                getHashMap.execute();
                getHashMap.close();
            });
        } else {
            try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement("DELETE FROM " + EterniaCrates.configs.tableItens + " WHERE crate=? AND item=?")) {
                getHashMap.setString(1, cratesName);
                getHashMap.setBytes(2, cratesData.itensId.get(id).serializeAsBytes());
                getHashMap.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        cratesData.itensId.remove(id.intValue());
        cratesData.itensChance.remove(id.intValue());
        player.sendMessage(EterniaCrates.msg.getMessage(Messages.ITEM_REMOVED, true));
    }

}