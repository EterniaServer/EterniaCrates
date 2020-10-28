package br.com.eterniaserver.eterniacrates;

import br.com.eterniaserver.eterniacrates.configurations.configs.ConfigsCfg;
import br.com.eterniaserver.eterniacrates.configurations.configs.TableCfg;
import br.com.eterniaserver.eterniacrates.configurations.locales.CommandsLocaleCfg;
import br.com.eterniaserver.eterniacrates.configurations.locales.MsgCfg;

import br.com.eterniaserver.eterniacrates.events.PlayerHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class EterniaCrates extends JavaPlugin {

    public static final ConfigsCfg configs = new ConfigsCfg();
    public static final CommandsLocaleCfg commands = new CommandsLocaleCfg();
    public static final MsgCfg msg = new MsgCfg();

    @Override
    public void onEnable() {

        new TableCfg();
        new Managers();

        this.getServer().getPluginManager().registerEvents(new PlayerHandler(), this);

    }

}
