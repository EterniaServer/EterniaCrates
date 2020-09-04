package br.com.eterniaserver.eterniacrates.objects;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CratesData {

    public final List<ItemStack> itensId = new ArrayList<>();
    private final Map<Double, ItemStack> itens = new TreeMap<>(Collections.reverseOrder());
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

    public Map<Double, ItemStack> getItens() {
        return itens;
    }

    public void addItens(Double chance, ItemStack itemStack) {
        itensId.add(itemStack);
        itens.put(chance, itemStack);
    }

    public String getCratesLocation() {
        return cratesLocation;
    }

    public void setCratesLocation(String cratesLocation) {
        this.cratesLocation = cratesLocation;
    }

}
