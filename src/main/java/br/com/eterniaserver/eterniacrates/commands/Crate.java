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
import br.com.eterniaserver.eternialib.NBTItem;
import br.com.eterniaserver.eternialib.SQL;
import br.com.eterniaserver.eternialib.sql.queries.Delete;
import br.com.eterniaserver.eternialib.sql.queries.Insert;
import br.com.eterniaserver.eternialib.sql.queries.Select;

import br.com.eterniaserver.eternialib.sql.queries.Update;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@CommandAlias("%crate")
public class Crate extends BaseCommand {

    public Crate() {
        try (Connection connection = SQL.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(new Select(EterniaCrates.configs.tableUsersCooldown).queryString()); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                APIServer.putUserCooldown(resultSet.getString(Constants.UUID), resultSet.getLong(Constants.COOLDOWN));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        try (Connection connection = SQL.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(new Select(EterniaCrates.configs.tableCrates).queryString()); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String cratesName = resultSet.getString(Constants.CRATE);
                CrateData cratesData = new CrateData(cratesName);
                cratesData.setCooldown(resultSet.getInt(Constants.COOLDOWN));
                byte[] key = resultSet.getBytes("cratekey");
                if (key != null) {
                    cratesData.setKey(ItemStack.deserializeBytes(key));
                }
                String locString = resultSet.getString(Constants.LOCATION);
                if (locString != null) {
                    cratesData.setCratesLocation(locString);
                }
                APIServer.putCrate(cratesName, cratesData);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        try (Connection connection = SQL.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(new Select(EterniaCrates.configs.tableItens).queryString()); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String cratesName = resultSet.getString(Constants.CRATE);
                CrateData crateData = APIServer.getCrate(cratesName);
                byte[] bytes = resultSet.getBytes("item");
                double chance = resultSet.getDouble("chance");
                crateData.addItens(chance, ItemStack.deserializeBytes(bytes));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
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
    public void onGiveKey(CommandSender sender, OnlinePlayer onlinePlayer, String cratesName, int amount) {
        Player player = onlinePlayer.getPlayer();
        cratesName = cratesName.toLowerCase();
        if (!APIServer.existsCrate(cratesName)) {
            sender.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_NOT_FOUND, true, cratesName));
            return;
        }

        if (APIServer.getCrate(cratesName).getKey() == null) {
            sender.sendMessage(EterniaCrates.msg.getMessage(Messages.NO_ITEM, true));
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

        Insert insert = new Insert(EterniaCrates.configs.tableCrates);
        insert.columns.set(Constants.CRATE);
        insert.values.set(cratesName);
        SQL.executeAsync(insert);

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

        Update update = new Update(EterniaCrates.configs.tableCrates);
        update.set.set(Constants.COOLDOWN, cooldown);
        update.where.set(Constants.CRATE, cratesName);
        SQL.executeAsync(update);

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

        final ItemStack itemStack = new ItemStack(player.getInventory().getItemInMainHand());
        if (itemStack.getType().equals(Material.AIR) || itemStack.getItemMeta() == null) {
            user.sendMessage(Messages.NO_AIR);
            return;
        }

        CrateData crateData = APIServer.getCrate(cratesName);
        crateData.addItens(chance, itemStack);

        try (Connection connection = SQL.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + EterniaCrates.configs.tableItens + " (crate, `item`, chance) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, cratesName);
            preparedStatement.setBytes(2, itemStack.serializeAsBytes());
            preparedStatement.setDouble(3, chance);
            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
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

        Delete delete = new Delete(EterniaCrates.configs.tableItens);
        delete.where.set(Constants.CRATE, cratesName);
        SQL.executeAsync(delete);

        delete = new Delete(EterniaCrates.configs.tableCrates);
        delete.where.set(Constants.CRATE, cratesName);
        SQL.executeAsync(delete);

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

        ItemStack tempItem = new ItemStack(player.getInventory().getItemInMainHand());
        if (tempItem.getType().equals(Material.AIR)) {
            user.sendMessage(Messages.NO_AIR);
            return;
        }

        CrateData cratesData = APIServer.getCrate(cratesName);
        NBTItem item = new NBTItem(tempItem);
        item.setInteger(Constants.ETERNIA_KEY, cratesName.hashCode());
        ItemStack itemStack = item.getItem();
        player.getInventory().setItemInMainHand(itemStack);
        cratesData.setKey(itemStack);

        try (Connection connection = SQL.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + EterniaCrates.configs.tableCrates + " SET cratekey=? WHERE crate=?")) {
            preparedStatement.setBytes(1, itemStack.serializeAsBytes());
            preparedStatement.setString(2, cratesName);
            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
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
    public void onGiveKeyAll(CommandSender sender, String cratesName, int amount) {
        cratesName = cratesName.toLowerCase();
        if (!APIServer.existsCrate(cratesName)) {
            sender.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_NOT_FOUND, true, cratesName));
            return;
        }

        ItemStack itemStack = APIServer.getCrate(cratesName).getKey();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack item = new ItemStack(itemStack);
            item.setAmount(amount);
            player.getInventory().addItem(item);
            player.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_KEY_RECEIVED, true, String.valueOf(amount), cratesName));
        }
        sender.sendMessage(EterniaCrates.msg.getMessage(Messages.CRATE_KEY_SEND, true, String.valueOf(amount), cratesName, "todos", "todos"));
    }

    @Subcommand("%crate_removeitem")
    @Syntax("%crate_removeitem_syntax")
    @CommandPermission("%crate_removeitem_perm")
    @Description("%crate_removeitem_description")
    public void removeItem(CommandSender player, String cratesName, int id) {
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

        try (Connection connection = SQL.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + EterniaCrates.configs.tableItens + " WHERE crate=? AND item=?")) {
            preparedStatement.setString(1, cratesName);
            preparedStatement.setBytes(2, cratesData.itensId.get(id).serializeAsBytes());
            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        cratesData.itensId.remove(id);
        cratesData.itensChance.remove(id);
        player.sendMessage(EterniaCrates.msg.getMessage(Messages.ITEM_REMOVED, true));
    }

}