package br.com.eterniaserver.eterniacrates.core.configurations;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.core.enums.Strings;
import br.com.eterniaserver.eternialib.configuration.CommandLocale;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.configuration.interfaces.ReloadableConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CrateConfiguration implements ReloadableConfiguration {

    private final EterniaCrates plugin;

    private final FileConfiguration inFile;
    private final FileConfiguration outFile;

    public CrateConfiguration(EterniaCrates plugin) {
        this.plugin = plugin;
        this.inFile = YamlConfiguration.loadConfiguration(new File(getFilePath()));
        this.outFile = new YamlConfiguration();
    }

    @Override
    public FileConfiguration inFileConfiguration() {
        return inFile;
    }

    @Override
    public FileConfiguration outFileConfiguration() {
        return outFile;
    }

    @Override
    public String getFolderPath() {
        return Constants.DATA_LAYER_FOLDER_PATH;
    }

    @Override
    public String getFilePath() {
        return Constants.CONFIG_FILE_PATH;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.GENERIC;
    }

    @Override
    public void executeConfig() {
        String[] strings = plugin.strings();

        strings[Strings.TABLE_USERS_COOLDOWN.ordinal()] = inFile.getString("sql.table-cooldown", "ec_cooldown");
        strings[Strings.TABLE_ITEMS.ordinal()] = inFile.getString("sql.table-items", "ec_items");
        strings[Strings.TABLE_CRATES.ordinal()] = inFile.getString("sql.table-crates", "ec_crates");

        outFile.set("server.prefix", strings[Strings.SERVER_PREFIX.ordinal()]);
        outFile.set("sql.table-cooldown", strings[Strings.TABLE_USERS_COOLDOWN.ordinal()]);
        outFile.set("sql.table-items", strings[Strings.TABLE_ITEMS.ordinal()]);
        outFile.set("sql.table-crates", strings[Strings.TABLE_CRATES.ordinal()]);
    }

    @Override
    public void executeCritical() { }
}
