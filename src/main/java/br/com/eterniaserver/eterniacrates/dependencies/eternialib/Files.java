package br.com.eterniaserver.eterniacrates.dependencies.eternialib;

import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.generics.PluginMSGs;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Files {

    private final EterniaCrates plugin;

    public Files(EterniaCrates plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        loadFile("config.yml", EterniaCrates.serverConfig);
    }

    public void loadMessages() {
        loadFile("messages.yml", EterniaCrates.msgConfig);
    }

    public void loadDatabase() {
        new Table();
    }

    private void loadFile(String fileName, YamlConfiguration yamlConfiguration) {

        final File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        if (!file.canRead()) {
            plugin.getServer().getConsoleSender().sendMessage(
                    PluginMSGs.getColor("&8[&aE&9S&8] &7A jar do EterniaServer não possui o arquivo necessário&8: &3" + fileName + "&8."));
        } else try {
            yamlConfiguration.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

    }

}
