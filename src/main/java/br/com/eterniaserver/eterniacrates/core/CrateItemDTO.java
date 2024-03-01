package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eterniacrates.core.Entities.CrateItem;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter
@Setter
public class CrateItemDTO {

    private Integer id;

    private final ItemStack item;
    private final Double chance;

    protected CrateItemDTO(CrateItem crateItem) {
        this.id = crateItem.getId();

        this.item = ItemStack.deserializeBytes(crateItem.getItem());
        this.chance = crateItem.getChance();
    }

    protected CrateItemDTO(ItemStack itemStack, Double chance) {
        this.item = itemStack;
        this.chance = chance;
    }

    public Optional<Integer> getId() {
        return Optional.ofNullable(id);
    }

}
