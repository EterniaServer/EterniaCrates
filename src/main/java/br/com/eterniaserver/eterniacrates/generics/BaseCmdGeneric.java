package br.com.eterniaserver.eterniacrates.generics;

import br.com.eterniaserver.acf.BaseCommand;
import br.com.eterniaserver.acf.CommandHelp;
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
import br.com.eterniaserver.eterniacrates.objects.CratesData;
import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.NBTItem;
import br.com.eterniaserver.eternialib.UUIDFetcher;
import br.com.eterniaserver.eternialib.sql.Connections;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class BaseCmdGeneric extends BaseCommand {

    public BaseCmdGeneric() {
        Map<String, String> temp = EQueries.getMapString(PluginConstants.getQuerySelectAll(PluginConfigs.TABLE_USERS), "uuid", "cooldown");
        temp.forEach((k, v) -> PluginVars.usersCooldown.put(k, Long.parseLong(v)));

        if (EterniaLib.getMySQL()) {
            EterniaLib.getConnections().executeSQLQuery(connection -> {
                final PreparedStatement getHashMap = connection.prepareStatement(PluginConstants.getQuerySelectAll(PluginConfigs.TABLE_CRATES));
                final ResultSet resultSet = getHashMap.executeQuery();
                getCrates(resultSet);
                getHashMap.close();
                resultSet.close();
            });
            EterniaLib.getConnections().executeSQLQuery(connection -> {
                final PreparedStatement getHashMap = connection.prepareStatement(PluginConstants.getQuerySelectAll(PluginConfigs.TABLE_ITENS));
                final ResultSet resultSet = getHashMap.executeQuery();
                getItens(resultSet);
                getHashMap.close();
                resultSet.close();
            });
        } else {
            try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement(PluginConstants.getQuerySelectAll(PluginConfigs.TABLE_CRATES)); ResultSet resultSet = getHashMap.executeQuery()) {
                getCrates(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement(PluginConstants.getQuerySelectAll(PluginConfigs.TABLE_ITENS)); ResultSet resultSet = getHashMap.executeQuery()) {
                getItens(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    private void getItens(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            final String cratesName = resultSet.getString("crate");
            final CratesData cratesData = PluginVars.cratesNameMap.get(cratesName);
            final byte[] bytes = resultSet.getBytes("item");
            final double chance = resultSet.getDouble("chance");
            cratesData.addItens(chance, ItemStack.deserializeBytes(bytes));
            cratesData.sort();
            if (cratesData.getCratesLocation() != null) {
                PluginVars.cratesDataMap.put(cratesData.getCratesLocation(), cratesData);
            }
            PluginVars.cratesNameMap.put(cratesName, cratesData);
        }
    }

    private void getCrates(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            final String cratesName = resultSet.getString("crate");
            final CratesData cratesData = new CratesData(cratesName);
            cratesData.setCooldown(resultSet.getInt("cooldown"));
            final byte[] key = resultSet.getBytes("cratekey");
            if (key != null) {
                cratesData.setKey(ItemStack.deserializeBytes(key));
            }
            final String locString = resultSet.getString("location");
            if (locString != null) {
                cratesData.setCratesLocation(locString);
                PluginVars.cratesDataMap.put(locString, cratesData);
            }
            PluginVars.cratesNameMap.put(cratesName, cratesData);
        }
    }

    @CommandAlias("crate")
    @CommandPermission("eternia.crate.admin")
    public class Crates extends BaseCommand {

        @Default
        @Description(" Ajuda para o comando de Crates")
        @Syntax("<página>")
        @HelpCommand
        public void onHelp(CommandHelp help) {
            help.showHelp();
        }

        @Subcommand("create")
        @Syntax("<nome da caixa>")
        @Description(" Cria uma nova caixa")
        public void onCrateCreate(CommandSender player, String cratesName) {
            if (!PluginVars.cratesNameMap.containsKey(cratesName)) {
                APICrates.createCrate(cratesName);
                EQueries.executeQuery(PluginConstants.getQueryInsert(PluginConfigs.TABLE_CRATES, "(crate)", "('" + cratesName + "')"));
                player.sendMessage(PluginMSGs.CREATE.replace("%crate%", cratesName));
            } else {
                player.sendMessage(PluginMSGs.ALREADY.replace("%crate%", cratesName));
            }
        }

        @Subcommand("cooldown")
        @Syntax("<caixa> <tempo>")
        @Description(" Define o tempo para abrir uma caixa")
        public void onCrateCooldown(CommandSender player, String cratesName, int cooldown) {
            if (APICrates.existsCrate(cratesName)) {
                final CratesData cratesData = APICrates.getCrate(cratesName);
                cratesData.setCooldown(cooldown);
                if (cratesData.getCratesLocation() != null) {
                    PluginVars.cratesDataMap.put(cratesData.getCratesLocation(), cratesData);
                }
                PluginVars.cratesNameMap.put(cratesName, cratesData);
                EQueries.executeQuery(PluginConstants.getQueryUpdate(PluginConfigs.TABLE_CRATES,  "cooldown", cooldown, "crate", cratesName));
                player.sendMessage(PluginMSGs.COOLDOWN_SET.replace("%time%", String.valueOf(cooldown)));
            } else {
                player.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("location")
        @Syntax("<caixa>")
        @Description(" Define a localização de uma caixa")
        public void onCrateLocation(Player player, String cratesName) {
            if (APICrates.existsCrate(cratesName)) {
                PluginVars.cacheSetLoc.put(UUIDFetcher.getUUIDOf(player.getName()), cratesName);
                player.sendMessage(PluginMSGs.SET_LOC);
            } else {
                player.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("putitem")
        @Syntax("<caixa> <chance>")
        @Description(" Adiciona o item da sua mão a uma caixa")
        public void onCrateAddItem(Player player, String cratesName, @Conditions("limits:min=0,max=1") Double chance) {
            if (PluginVars.cratesNameMap.containsKey(cratesName)) {
                final CratesData cratesData = PluginVars.cratesNameMap.get(cratesName);
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                cratesData.addItens(chance, itemStack);
                cratesData.sort();
                if (cratesData.getCratesLocation() != null) {
                    PluginVars.cratesDataMap.put(cratesData.getCratesLocation(), cratesData);
                }
                PluginVars.cratesNameMap.put(cratesName, cratesData);
                if (EterniaLib.getMySQL()) {
                    EterniaLib.getConnections().executeSQLQuery(connection -> {
                        final PreparedStatement getHashMap = connection.prepareStatement("INSERT INTO " + PluginConfigs.TABLE_ITENS + " (crate, `item`, chance) VALUES (?, ?, ?)");
                        getHashMap.setString(1, cratesName);
                        getHashMap.setBytes(2, itemStack.serializeAsBytes());
                        getHashMap.setDouble(3, chance);
                        getHashMap.execute();
                        getHashMap.close();
                    });
                } else {
                    try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement("INSERT INTO " + PluginConfigs.TABLE_ITENS + " (crate, `item`, chance) VALUES (?, ?, ?)")) {
                        getHashMap.setString(1, cratesName);
                        getHashMap.setBytes(2, itemStack.serializeAsBytes());
                        getHashMap.setDouble(3, chance);
                        getHashMap.execute();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                player.sendMessage(PluginMSGs.ITEM_ADD);
            } else {
                player.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("setkey")
        @Syntax("<caixa>")
        @Description(" Adiciona o item da sua mão como chave para a caixa")
        public void onCrateSetKey(Player player, String cratesName) {
            if (PluginVars.cratesNameMap.containsKey(cratesName)) {
                final CratesData cratesData = PluginVars.cratesNameMap.get(cratesName);
                ItemStack tempitem = player.getInventory().getItemInMainHand();
                NBTItem item = new NBTItem(tempitem);
                item.setString("EterniaKey", cratesName);
                final ItemStack itemStack = item.getItem();
                player.getInventory().setItemInMainHand(itemStack);
                cratesData.setKey(itemStack);
                if (EterniaLib.getMySQL()) {
                    EterniaLib.getConnections().executeSQLQuery(connection -> {
                        final PreparedStatement getHashMap = connection.prepareStatement("UPDATE " + PluginConfigs.TABLE_CRATES + " SET cratekey=? WHERE crate=?");
                        getHashMap.setBytes(1, itemStack.serializeAsBytes());
                        getHashMap.setString(2, cratesName);
                        getHashMap.execute();
                        getHashMap.close();
                    });
                } else {
                    try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement("UPDATE " + PluginConfigs.TABLE_CRATES + " SET cratekey=? WHERE crate=?")) {
                        getHashMap.setBytes(1, itemStack.serializeAsBytes());
                        getHashMap.setString(2, cratesName);
                        getHashMap.execute();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                PluginVars.cratesNameMap.put(cratesName, cratesData);
                if (cratesData.getCratesLocation() != null) {
                    PluginVars.cratesDataMap.put(cratesData.getCratesLocation(), cratesData);
                }
                player.sendMessage(PluginMSGs.KEY_SET);
            } else {
                player.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("givekey")
        @Syntax("<jogador> <caixa> <quantia>")
        @CommandCompletion("@players caixa 1")
        @Description(" Dê a um jogador uma chave de uma caixa")
        public void onGiveKey(CommandSender sender, OnlinePlayer onlinePlayer, String cratesName, Integer amount) {
            Player player = onlinePlayer.getPlayer();
            if (PluginVars.cratesNameMap.containsKey(cratesName)) {
                ItemStack itemStack = new ItemStack(PluginVars.cratesNameMap.get(cratesName).getKey());
                itemStack.setAmount(amount);
                player.getInventory().addItem(itemStack);
                sender.sendMessage(PluginMSGs.KEY_GIVE);
                player.sendMessage(PluginMSGs.KEY_RECEIVE);
            } else {
                player.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("givekeyall")
        @Syntax("<caixa> <quantia>")
        @Description(" Dê a todos uma chave de uma caixa")
        public void onGiveKeyAll(CommandSender sender, String cratesName, Integer amount) {
            if (APICrates.existsCrate(cratesName)) {
                ItemStack itemStack = new ItemStack(PluginVars.cratesNameMap.get(cratesName).getKey());
                itemStack.setAmount(amount);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().addItem(new ItemStack(itemStack));
                    player.sendMessage(PluginMSGs.KEY_RECEIVE);
                }
                sender.sendMessage(PluginMSGs.KEY_GIVE);
            } else {
                sender.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("listitens")
        @Syntax("<caixa>")
        @Description(" Veja cada item que uma caixa possui")
        public void listItens(CommandSender player, String cratesName) {
            if (APICrates.existsCrate(cratesName)) {
                final CratesData cratesData = APICrates.getCrate(cratesName);
                player.sendMessage(PluginMSGs.LIST_TITLE.replace("%crate%", cratesName));
                int index = 0;
                for (ItemStack itemStack : cratesData.itensId) {
                    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, Bukkit.getItemFactory().hoverContentOf(itemStack));
                    String name = "";
                    if (itemStack.getItemMeta() != null) {
                        name = itemStack.getItemMeta().getDisplayName();
                        if (name.equals("")) {
                            name = itemStack.getI18NDisplayName();
                        }
                    }
                    TextComponent component = new TextComponent(PluginMSGs.LIST_ITENS.replace("%id%", String.valueOf(index)).replace("%item%", "x" + itemStack.getAmount() + " " + name));
                    component.setHoverEvent(event);
                    player.sendMessage(component);
                    index++;
                }
            } else {
                player.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("chance")
        @Syntax("<caixa>")
        @Description(" Chance de cada item")
        public void onChance(CommandSender sender, String cratesName) {
            if (APICrates.existsCrate(cratesName)) {
                final CratesData cratesData = APICrates.getCrate(cratesName);
                sender.sendMessage(PluginMSGs.LIST_TITLE.replace("%crate%", cratesName));
                cratesData.getItens().forEach((k, v) -> {
                    HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, Bukkit.getItemFactory().hoverContentOf(v));
                    String name = "";
                    if (v.getItemMeta() != null) {
                        name = v.getItemMeta().getDisplayName();
                        if (name.equals("")) {
                            name = v.getI18NDisplayName();
                        }
                    }
                    TextComponent component = new TextComponent(PluginMSGs.LIST_ITENS.replace("%id%", String.valueOf(k)).replace("%item%", "x" + v.getAmount() + " " + name));
                    component.setHoverEvent(event);
                    sender.sendMessage(component);
                });
            } else {
                sender.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("removeitem")
        @Syntax("<caixa> <id>")
        @Description(" Remove um item de uma caixa")
        public void removeItem(CommandSender player, String cratesName, Integer id) {
            if (APICrates.existsCrate(cratesName)) {
                final CratesData cratesData = APICrates.getCrate(cratesName);
                if (cratesData.itensId.get(id) != null) {
                        cratesData.getItens().entrySet().removeIf(entry -> (cratesData.itensId.get(id).equals(entry.getValue())));
                    if (EterniaLib.getMySQL()) {
                        EterniaLib.getConnections().executeSQLQuery(connection -> {
                            final PreparedStatement getHashMap = connection.prepareStatement("DELETE FROM " + PluginConfigs.TABLE_ITENS + " WHERE crate=? AND item=?");
                            getHashMap.setString(1, cratesName);
                            getHashMap.setBytes(2, cratesData.itensId.get(id).serializeAsBytes());
                            getHashMap.execute();
                            getHashMap.close();
                        });
                    } else {
                        try (PreparedStatement getHashMap = Connections.getSQLite().prepareStatement("DELETE FROM " + PluginConfigs.TABLE_ITENS + " WHERE crate=? AND item=?")) {
                            getHashMap.setString(1, cratesName);
                            getHashMap.setBytes(2, cratesData.itensId.get(id).serializeAsBytes());
                            getHashMap.execute();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    cratesData.itensId.remove(cratesData.itensId.get(id));
                    PluginVars.cratesNameMap.put(cratesName, cratesData);
                    PluginVars.cratesDataMap.put(cratesData.getCratesLocation(), cratesData);
                    player.sendMessage(PluginMSGs.REMOVE_ITEM);
                } else {
                    player.sendMessage(PluginMSGs.NO_ITEM);
                }
            } else {
                player.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

        @Subcommand("removecrate")
        @Syntax("<caixa> <id>")
        @Description(" Remove uma caixa")
        public void removeCaixa(CommandSender sender, String cratesName) {
            if (APICrates.existsCrate(cratesName)) {
                String cratesLoc = APICrates.getCrate(cratesName).getCratesLocation();
                PluginVars.cratesNameMap.remove(cratesName);
                PluginVars.cratesDataMap.remove(cratesLoc);
                EQueries.executeQuery(PluginConstants.getQueryDelete(PluginConfigs.TABLE_ITENS, "crate", cratesName));
                EQueries.executeQuery(PluginConstants.getQueryDelete(PluginConfigs.TABLE_CRATES, "crate", cratesName));
                sender.sendMessage(PluginMSGs.DELETED);
            } else {
                sender.sendMessage(PluginMSGs.NO_EXISTS.replace("%crate%", cratesName));
            }
        }

    }

}
