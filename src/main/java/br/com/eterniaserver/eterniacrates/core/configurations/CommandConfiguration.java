package br.com.eterniaserver.eterniacrates.core.configurations;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.core.enums.Commands;
import br.com.eterniaserver.eternialib.configuration.CommandLocale;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.configuration.interfaces.CmdConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CommandConfiguration implements CmdConfiguration<Commands> {

    private final FileConfiguration inFile;
    private final FileConfiguration outFile;

    public CommandConfiguration() {
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
        return Constants.DATA_LOCALE_FOLDER_PATH;
    }

    @Override
    public String getFilePath() {
        return Constants.COMMANDS_FILE_PATH;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.BLOCKED;
    }

    @Override
    public void executeConfig() { }

    @Override
    public void executeCritical() {
        addCommandLocale(Commands.CRATE, new CommandLocale(
                "crate",
                 " <página>",
                " Ajuda para o sistema de caixas",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_KEY, new CommandLocale(
                "givekey",
                " <caixa> <quantia>",
                " Dê uma quantia de chaves de uma caixa a um jogador",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_CREATE, new CommandLocale(
                "create",
                " <caixa>",
                " Crie uma nova caixa",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_COOLDOWN, new CommandLocale(
                "cooldown",
                " <caixa> <cooldown>",
                " Define um cooldown para uma caixa",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_LOCATION, new CommandLocale(
                "location",
                " <caixa>",
                " Define a localização de uma caixa",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_PUT_ITEM, new CommandLocale(
                "putitem",
                " <caixa> <chance>",
                " Define um item e a chance de ganhar ele em uma caixa",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_LIST_ITEMS, new CommandLocale(
                "listitens",
                " <caixa>",
                " Mostra a lista de itens de uma caixa",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_DELETE, new CommandLocale(
                "delete",
                " <caixa>",
                " Delete uma caixa",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_SET_KEY, new CommandLocale(
                "setkey",
                " <caixa>",
                " Adiciona o item da sua mão como uma chave para uma caixa",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_NBT, new CommandLocale(
                "getnbt",
                " <caixa>",
                " Pegue o NBT de uma caixa",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_GIVE_KEY_ALL, new CommandLocale(
                "givekeyall",
                " <caixa> <quantia>",
                " Dê uma quantia de chaves de uma caixa a todos online",
                "eternia.crate.admin",
                null
        ));
        addCommandLocale(Commands.CRATE_REMOVE_ITEM, new CommandLocale(
                "removeitem",
                " <caixa> <id>",
                " Remova um item de uma caixa",
                "eternia.crate.admin",
                null
        ));
    }
}
