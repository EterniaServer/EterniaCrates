package br.com.eterniaserver.eterniacrates.objects;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CratesData {

    public final List<ItemStack> itensId = new ArrayList<>();
    public final List<Double> itensChance = new ArrayList<>();

    private final String cratesName;
    private String cratesLocation;
    private ItemStack key;
    private int cooldown;

    public CratesData(String cratesName) {
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

    public String getCratesLocation() {
        return cratesLocation;
    }

    public void setCratesLocation(String cratesLocation) {
        this.cratesLocation = cratesLocation;
    }

    private void sort() {
        for (int i = 0; i < itensId.size(); i++) {
            for (int j = 0; j < itensId.size(); j++) {
                if (itensChance.get(i) < itensChance.get(j)) {
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

}
