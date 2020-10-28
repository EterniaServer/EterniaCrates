package br.com.eterniaserver.eterniacrates.objects;

import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.enums.Messages;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CrateData {

    public final List<ItemStack> itensId = new ArrayList<>();
    public final List<Double> itensChance = new ArrayList<>();

    private final String cratesName;
    private String cratesLocation;
    private ItemStack key;
    private int cooldown;

    public CrateData(String cratesName) {
        this.cratesName = cratesName;
    }

    public String getCratesName() {
        return cratesName;
    }

    public ItemStack getKey() {
        return key;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setKey(ItemStack key) {
        this.key = key;
    }

    public void addItens(Double chance, ItemStack itemStack) {
        itensId.add(itemStack);
        itensChance.add(chance);
        sort();
    }

    public boolean isValid(Block block) {
        String loc = block.getX() + ":" + block.getY() + ":" + block.getZ();
        return loc.equals(cratesLocation);
    }

    public void setCratesLocation(String string) {
        this.cratesLocation = string;
    }

    private void sort() {
        for (int i = 0; i < itensId.size(); i++) {
            for (int j = 0; j < itensId.size(); j++) {
                if (itensChance.get(i) > itensChance.get(j)) {
                    double tempDouble = itensChance.get(j);
                    ItemStack tempStack = itensId.get(j);
                    itensChance.set(j, itensChance.get(i));
                    itensId.set(j, itensId.get(i));
                    itensChance.set(i, tempDouble);
                    itensId.set(i, tempStack);
                }
            }
        }
    }

    public void displayItens(User user) {
        user.sendMessage(Messages.CRATE_TITLE, cratesName);
        for (int i = 0; i < itensChance.size(); i++) {
            ItemStack v = itensId.get(i);
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, Bukkit.getItemFactory().hoverContentOf(v));
            String name = v.getItemMeta().getDisplayName();
            TextComponent component = new TextComponent(EterniaCrates.msg.getMessage(Messages.CRATE_ITENS, true, String.valueOf(i), "x" + v.getAmount() + " " + name, (itensChance.get(i) * 100) + "%"));
            component.setHoverEvent(event);
            user.sendComponent(component);
        }
    }

}
