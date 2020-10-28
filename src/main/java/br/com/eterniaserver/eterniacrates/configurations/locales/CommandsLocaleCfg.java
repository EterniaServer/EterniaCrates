package br.com.eterniaserver.eterniacrates.configurations.locales;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.core.APIServer;
import br.com.eterniaserver.eterniacrates.enums.Commands;
import br.com.eterniaserver.eterniacrates.objects.CommandLocale;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandsLocaleCfg {

    private final String[] commands;
    private final String[] syntax;
    private final String[] descriptions;
    private final String[] perms;
    private final String[] aliases;

    public CommandsLocaleCfg() {

        Commands[] commandsList = Commands.values();
        this.commands = new String[Commands.values().length];
        this.syntax = new String[Commands.values().length];
        this.descriptions = new String[Commands.values().length];
        this.perms = new String[Commands.values().length];
        this.aliases = new String[Commands.values().length];

        Map<String, CommandLocale> defaults = new HashMap<>();

        this.addDefault(defaults, Commands.CRATE, "crate", "eternia.crate.admin", " <página>", " Ajuda para o sistema de caixas", null);
        this.addDefault(defaults, Commands.CRATE_KEY, "key|givekey", "eternia.crate.admin", " <caixa> <quantia>", " Dê uma quantia de chaves de uma caixa a um jogador", null);
        this.addDefault(defaults, Commands.CRATE_CREATE, "create", "eternia.crate.admin", " <caixa>", " Crie uma nova caixa", null);
        this.addDefault(defaults, Commands.CRATE_COOLDOWN, "cooldown", "eternia.crate.admin", " <caixa> <cooldown>", " Define um cooldown para uma caixa", null);
        this.addDefault(defaults, Commands.CRATE_LOCATION, "location", "eternia.crate.admin", " <caixa>", " Define a localização de uma caixa", null);
        this.addDefault(defaults, Commands.CRATE_PUT_ITEM, "putitem", "eternia.crate.admin", " <caixa> <chance>", " Define um item e a chance de ganhar ele em uma caixa", null);
        this.addDefault(defaults, Commands.CRATE_LISTITENS, "listitens", "eternia.crate.admin", " <caixa>", " Mostra a lista de itens de uma caixa", null);
        this.addDefault(defaults, Commands.CRATE_DELETE, "delete", "eternia.crate.admin", " <caixa>", " Delete uma caixa", null);
        this.addDefault(defaults, Commands.CRATE_SETKEY, "setkey", "eternia.crate.admin", " <caixa>", " Adiciona o item da sua mão como uma chave para uma caixa", null);
        this.addDefault(defaults, Commands.CRATE_NBT, "getnbt", "eternia.crate.admin", " <caixa>", " Pegue o NBT de uma caixa", null);
        this.addDefault(defaults, Commands.CRATE_GIVEKEYALL, "givekeyall", "eternia.crate.admin", " <caixa> <quantia>", " Dê uma quantia de chaves de uma caixa a todos online", null);
        this.addDefault(defaults, Commands.CRATE_REMOVEITEM, "removeitem", "eternia.crate.admin", " <caixa> <id>", " Remova um item de uma caixa", null);

        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Constants.COMMANDS_FILE_PATH));

        for (Commands commandsEnum : commandsList) {
            CommandLocale commandLocale = defaults.get(commandsEnum.name());

            this.commands[commandsEnum.ordinal()] = config.getString(commandsEnum.name() + ".name", commandLocale.name);
            config.set(commandsEnum.name() + ".name", this.commands[commandsEnum.ordinal()]);

            if (commandLocale.syntax != null) {
                this.syntax[commandsEnum.ordinal()] = config.getString(commandsEnum.name() + ".syntax", commandLocale.syntax);
                config.set(commandsEnum.name() + ".syntax", this.syntax[commandsEnum.ordinal()]);
            }

            this.descriptions[commandsEnum.ordinal()] = config.getString(commandsEnum.name() + ".description", commandLocale.description);
            config.set(commandsEnum.name() + ".description", this.descriptions[commandsEnum.ordinal()]);

            this.perms[commandsEnum.ordinal()] = config.getString(commandsEnum.name() + ".perm", commandLocale.perm);
            config.set(commandsEnum.name() + ".perm", commandLocale.perm);

            if (commandLocale.aliases != null) {
                this.aliases[commandsEnum.ordinal()] = config.getString(commandsEnum.name() + ".aliases", commandLocale.aliases);
                config.set(commandsEnum.name() + ".aliases", this.aliases[commandsEnum.ordinal()]);
            }

        }

        if (new File(Constants.DATA_LOCALE_FOLDER_PATH).mkdir()) {
            APIServer.logError("Pasta de locales criada com sucesso", 1);
        }

        try {
            config.save(Constants.COMMANDS_FILE_PATH);
        } catch (IOException exception) {
            APIServer.logError("Impossível de criar arquivos em " + Constants.DATA_LOCALE_FOLDER_PATH, 3);
        }

        defaults.clear();

    }

    private void addDefault(Map<String, CommandLocale> defaults, Commands id, String name, String perm, String syntax, String description, String aliases) {
        CommandLocale commandLocale = new CommandLocale(id, name, syntax, description, perm, aliases);
        defaults.put(id.name(), commandLocale);
    }

    public String getName(Commands id) {
        return commands[id.ordinal()];
    }

    public String getSyntax(Commands id) {
        return syntax[id.ordinal()];
    }

    public String getDescription(Commands id) {
        return descriptions[id.ordinal()];
    }

    public String getPerm(Commands id) {
        return perms[id.ordinal()];
    }

    public String getAliases(Commands id) {
        return aliases[id.ordinal()];
    }

}
