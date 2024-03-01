package br.com.eterniaserver.eterniacrates;

import br.com.eterniaserver.eterniacrates.api.CrateAPI;
import br.com.eterniaserver.eterniacrates.core.enums.Strings;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;


public class EterniaCrates extends JavaPlugin {

    private final String[] strings = new String[Strings.values().length];

    @Getter
    private final NamespacedKey crateKey = NamespacedKey.minecraft(Constants.ETERNIA_CRATE);

    @Getter
    @Setter
    private static CrateAPI crateAPI;

    public String[] strings() {
        return strings;
    }

    public String getString(Strings stringEntry) {
        return strings[stringEntry.ordinal()];
    }

    @Override
    public void onEnable() {
        new Manager(this);
    }

}
