package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.core.Entities.Crate;

import br.com.eterniaserver.eterniacrates.core.enums.Messages;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.MessageOptions;
import lombok.Getter;
import lombok.Setter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

@Getter
@Setter
public class CrateDTO {

    private final String crateName;

    private int cooldown;
    private ItemStack crateKey;
    private String crateId;

    private List<CrateItemDTO> items;

    protected CrateDTO(EterniaCrates plugin, Crate crate) {
        this.crateName = crate.getCrate();

        if (crate.getCooldown() != null) {
            this.cooldown = crate.getCooldown();
        }

        if (crate.getCrateKey() != null) {
            this.crateKey = ItemStack.deserializeBytes(crate.getCrateKey());

            PersistentDataContainer dataContainer = this.crateKey.getItemMeta().getPersistentDataContainer();

            crateId = dataContainer.getOrDefault(plugin.getCrateKey(), PersistentDataType.STRING, "");
        }

        this.items = new ArrayList<>();
    }

    public void setItems(List<CrateItemDTO> items) {
        items.sort(Comparator.comparing(CrateItemDTO::getChance));

        this.items = items;
    }

    public boolean containsItem(Integer id) {
        return items.stream().anyMatch(i -> i.getId().isPresent() && i.getId().get().equals(id));
    }

    public void removeItem(Integer id) {
        items.removeIf(i -> i.getId().isPresent() &&i.getId().get().equals(id));
    }

    public void addItem(ItemStack itemStack, Double chance) {
        CrateItemDTO crateItemDTO = new CrateItemDTO(itemStack, chance);

        items.add(crateItemDTO);
        items.sort(Comparator.comparing(CrateItemDTO::getChance));
    }

    public void displayMessage(EterniaCrates plugin, Player player) {
        MessageOptions options = new MessageOptions(getCrateName());
        EterniaLib.getChatCommons().sendMessage(player, Messages.CRATE_TITLE, options);
        for (CrateItemDTO crateItemDTO : getItems()) {
            if (crateItemDTO.getId().isPresent()) {
                ItemStack itemStack = crateItemDTO.getItem();
                Component display = itemStack.getItemMeta().displayName();

                String displayStr = display != null
                        ? PlainTextComponentSerializer.plainText().serialize(display)
                        : itemStack.getType().toString();

                MessageOptions messageOptions = new MessageOptions(
                        String.valueOf(crateItemDTO.getId().get()),
                        "x" + itemStack.getAmount() + " " + displayStr,
                        crateItemDTO.getChance() * 100D + "%"
                );
                Component message = EterniaLib.getChatCommons().parseMessage(Messages.CRATE_ITENS, messageOptions);

                player.sendMessage(message.hoverEvent(
                        plugin.getServer().getItemFactory().asHoverEvent(itemStack, UnaryOperator.identity())
                ));
            }
        }
    }

}
