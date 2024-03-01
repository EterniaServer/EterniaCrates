package br.com.eterniaserver.eterniacrates.core;

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
import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.core.enums.Messages;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.MessageOptions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@CommandAlias("%CRATE")
public class CrateCommands extends BaseCommand {

    private final EterniaCrates plugin;

    public CrateCommands(EterniaCrates plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("%CRATE_DESCRIPTION")
    @Syntax("%CRATE_SYNTAX")
    @CommandPermission("%CRATE_PERM")
    @CatchUnknown
    @HelpCommand
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("%CRATE_KEY")
    @Syntax("%CRATE_KEY_SYNTAX")
    @CommandCompletion("@players @crates 1")
    @Description("%CRATE_KEY_DESCRIPTION")
    @CommandPermission("%CRATE_KEY_PERM")
    public void onGiveKey(CommandSender sender, OnlinePlayer onlinePlayer, String cratesName, int amount) {
        Player player = onlinePlayer.getPlayer();
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(sender, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        ItemStack key = EterniaCrates.getCrateAPI().read(cratesName).getCrateKey();
        if (key == null) {
            EterniaLib.getChatCommons().sendMessage(sender, Messages.NO_ITEM);
            return;
        }

        ItemStack itemStack = new ItemStack(key);
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);

        Component playerDisplay = player.displayName();
        String displayName = PlainTextComponentSerializer.plainText().serialize(playerDisplay);

        MessageOptions playerOptions = new MessageOptions(String.valueOf(amount), cratesName);
        EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_KEY_RECEIVED, playerOptions);
        MessageOptions senderOptions = new MessageOptions(String.valueOf(amount), cratesName, player.getName(), displayName);
        EterniaLib.getChatCommons().sendMessage(sender, Messages.CRATE_KEY_SEND, senderOptions);
    }

    @Subcommand("%CRATE_CREATE")
    @Syntax("%CRATE_CREATE_SYNTAX")
    @CommandPermission("%CRATE_CREATE_PERM")
    @Description("%CRATE_CREATE_DESCRIPTION")
    public void onCrateCreate(CommandSender player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        if (EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_ALREADY_EXIST, options);
            return;
        }

        EterniaCrates.getCrateAPI().create(cratesName);
        MessageOptions options = new MessageOptions(cratesName);
        EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_CREATED, options);
    }

    @Subcommand("%CRATE_COOLDOWN")
    @Syntax("%CRATE_COOLDOWN_SYNTAX")
    @CommandPermission("%CRATE_COOLDOWN_PERM")
    @CommandCompletion("@crates 30")
    @Description("%CRATE_COOLDOWN_DESCRIPTION")
    public void onCrateCooldown(CommandSender player, String cratesName, Integer cooldown) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        CrateDTO crate = EterniaCrates.getCrateAPI().read(cratesName);
        crate.setCooldown(cooldown);

        EterniaCrates.getCrateAPI().update(crate);

        MessageOptions options = new MessageOptions(cratesName, String.valueOf(cooldown));
        EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_SET_COOLDOWN, options);
    }

    @Subcommand("%CRATE_LOCATION")
    @Syntax("%CRATE_LOCATION_SYNTAX")
    @CommandPermission("%CRATE_LOCATION_PERM")
    @CommandCompletion("@crates")
    @Description("%CRATE_LOCATION_DESCRIPTION")
    public void onCrateLocation(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        EterniaCrates.getCrateAPI().putCachedLoc(player.getUniqueId(), cratesName);
        MessageOptions options = new MessageOptions(cratesName);
        EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_LOCATION, options);
    }

    @Subcommand("%CRATE_PUT_ITEM")
    @Syntax("%CRATE_PUT_ITEM_SYNTAX")
    @CommandPermission("%CRATE_PUT_ITEM_PERM")
    @Description("%CRATE_PUT_ITEM_DESCRIPTION")
    @CommandCompletion("@crates 0.2")
    public void onCrateAddItem(Player player, String cratesName, @Conditions("limits:min=0,max=1") Double chance) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        ItemStack itemStack = new ItemStack(player.getInventory().getItemInMainHand());
        if (itemStack.getType().equals(Material.AIR) || itemStack.getItemMeta() == null) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.NO_AIR);
            return;
        }

        CrateDTO crate = EterniaCrates.getCrateAPI().read(cratesName);
        crate.addItem(itemStack, chance);

        String itemName = EterniaLib.getChatCommons().plain(itemStack.displayName());

        EterniaCrates.getCrateAPI().addItem(crate);

        MessageOptions options = new MessageOptions(itemName);
        EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_ITEM_PUT, options);
    }

    @Subcommand("%CRATE_LIST_ITEMS")
    @Syntax("%CRATE_LIST_ITEMS_SYNTAX")
    @CommandPermission("%CRATE_LIST_ITEMS_PERM")
    @CommandCompletion("@crates")
    @Description("%CRATE_LIST_ITEMS_DESCRIPTION")
    public void listItems(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        EterniaCrates.getCrateAPI().read(cratesName).displayMessage(plugin, player);
    }

    @Subcommand("%CRATE_DELETE")
    @Syntax("%CRATE_DELETE_SYNTAX")
    @CommandPermission("%CRATE_DELETE_PERM")
    @Description("%CRATE_DELETE_DESCRIPTION")
    public void deleteCrate(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        EterniaCrates.getCrateAPI().delete(cratesName);
        EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_DELETED);
    }

    @Subcommand("%CRATE_SET_KEY")
    @Syntax("%CRATE_SET_KEY_SYNTAX")
    @CommandPermission("%CRATE_SET_KEY_PERM")
    @CommandCompletion("@crates")
    @Description("%CRATE_SET_KEY_DESCRIPTION")
    public void onCrateSetKey(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        ItemStack itemStack = new ItemStack(player.getInventory().getItemInMainHand());
        if (itemStack.getType().equals(Material.AIR)) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.NO_AIR);
            return;
        }

        CrateDTO crateDTO = EterniaCrates.getCrateAPI().read(cratesName);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(cratesName.hashCode());

        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(plugin.getCrateKey(), PersistentDataType.STRING, cratesName);

        itemStack.setItemMeta(itemMeta);

        player.getInventory().setItemInMainHand(itemStack);

        crateDTO.setCrateKey(itemStack);

        EterniaCrates.getCrateAPI().update(crateDTO);
        MessageOptions options = new MessageOptions(cratesName);
        EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_KEY_SETTED, options);
    }

    @Subcommand("%CRATE_NBT")
    @CommandPermission("%CRATE_NBT_PERM")
    @Syntax("%CRATE_NBT_SYNTAX")
    @CommandCompletion("@crates")
    @Description("%CRATE_NBT_DESCRIPTION")
    public void onGetNBT(Player player, String cratesName) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        MessageOptions options = new MessageOptions(String.valueOf(cratesName.hashCode()));
        EterniaLib.getChatCommons().sendMessage(player, Messages.KEY_NBT, options);
    }

    @Subcommand("%CRATE_REMOVE_ITEM")
    @Syntax("%CRATE_REMOVE_ITEM_SYNTAX")
    @CommandPermission("%CRATE_REMOVE_ITEM_PERM")
    @CommandCompletion("@crates 1")
    @Description("%CRATE_REMOVE_ITEM_DESCRIPTION")
    public void removeItem(CommandSender sender, String cratesName, int id) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(sender, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        CrateDTO crateDTO = EterniaCrates.getCrateAPI().read(cratesName);
        if (crateDTO.containsItem(id)) {
            EterniaLib.getChatCommons().sendMessage(sender, Messages.NO_ITEM);
            return;
        }

        EterniaCrates.getCrateAPI().removeItem(cratesName, id);
        EterniaLib.getChatCommons().sendMessage(sender, Messages.ITEM_REMOVED);
    }

    @Subcommand("%CRATE_GIVE_KEY_ALL")
    @Syntax("%CRATE_GIVE_KEY_ALL_SYNTAX")
    @CommandPermission("%CRATE_GIVE_KEY_ALL_PERM")
    @CommandCompletion("@crates 1")
    @Description("%CRATE_GIVE_KEY_ALL_DESCRIPTION")
    public void onGiveKeyAll(CommandSender sender, String cratesName, int amount) {
        cratesName = cratesName.toLowerCase();
        if (!EterniaCrates.getCrateAPI().existsCrate(cratesName)) {
            MessageOptions options = new MessageOptions(cratesName);
            EterniaLib.getChatCommons().sendMessage(sender, Messages.CRATE_NOT_FOUND, options);
            return;
        }

        ItemStack itemStack = EterniaCrates.getCrateAPI().read(cratesName).getCrateKey();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack item = new ItemStack(itemStack);
            item.setAmount(amount);
            player.getInventory().addItem(item);
            MessageOptions options = new MessageOptions(String.valueOf(amount), cratesName);
            EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_KEY_RECEIVED, options);
        }

        MessageOptions options = new MessageOptions(String.valueOf(amount), cratesName, "todos", "todos");
        EterniaLib.getChatCommons().sendMessage(sender, Messages.CRATE_KEY_SEND, options);
    }

}