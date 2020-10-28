package br.com.eterniaserver.eterniacrates.configurations.configs;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.core.APIServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigsCfg {

    public final String serverPrefix;

    public final String tableUsersCooldown;
    public final String tableItens;
    public final String tableCrates;

    public ConfigsCfg() {

        FileConfiguration scheduleConfig = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        FileConfiguration outSchedule = new YamlConfiguration();

        this.serverPrefix = scheduleConfig.getString("server.prefix", "$8[$aE$9C$8]$7 ").replace('$', (char) 0x00A7);

        this.tableUsersCooldown = scheduleConfig.getString("sql.table-cooldown", "ec_cooldown");
        this.tableItens = scheduleConfig.getString("sql.table-itens", "ec_itens");
        this.tableCrates = scheduleConfig.getString("sql.table-crates", "ec_crates");

        outSchedule.set("server.prefix", this.serverPrefix);

        outSchedule.set("sql.table-cooldown", this.tableUsersCooldown);
        outSchedule.set("sql.table-itens", this.tableItens);
        outSchedule.set("sql.table-crates", this.tableCrates);
        outSchedule.options().header("Caso precise de ajuda acesse https://github.com/EterniaServer/EterniaServer/wiki");

        try {
            outSchedule.save(Constants.CONFIG_FILE_PATH);
        } catch (IOException exception) {
            APIServer.logError("Impossível de criar arquivos em " + Constants.DATA_LAYER_FOLDER_PATH, 3);
        }

    }

}