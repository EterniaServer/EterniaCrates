package br.com.eterniaserver.eterniacrates;

import br.com.eterniaserver.acf.ConditionFailedException;
import br.com.eterniaserver.eterniacrates.dependencies.eternialib.Files;
import br.com.eterniaserver.eterniacrates.generics.BaseCmdGeneric;
import br.com.eterniaserver.eterniacrates.generics.EventPlayerInteract;
import br.com.eterniaserver.eternialib.EterniaLib;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EterniaCrates extends JavaPlugin {

    public static final YamlConfiguration serverConfig = new YamlConfiguration();
    public static final YamlConfiguration msgConfig = new YamlConfiguration();

    private final Files files = new Files(this);

    @Override
    public void onEnable() {

        files.loadConfigs();
        files.loadMessages();
        files.loadDatabase();

        EterniaLib.getManager().enableUnstableAPI("help");
        EterniaLib.getManager().getCommandConditions().addCondition(Float.class, "limits", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (c.getConfigValue("min", 0) > value) {
                throw new ConditionFailedException("O valor mínimo precisa ser &3" + c.getConfigValue("min", 0));
            }
            if (c.getConfigValue("max", 3) < value) {
                throw new ConditionFailedException("O valor máximo precisa ser &3 " + c.getConfigValue("max", 3));
            }
        });
        EterniaLib.getManager().registerCommand(new BaseCmdGeneric());
        getServer().getPluginManager().registerEvents(new EventPlayerInteract(), this);

    }

}
