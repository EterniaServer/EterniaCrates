package br.com.eterniaserver.eterniacrates.configurations.locales;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.core.APIServer;
import br.com.eterniaserver.eterniacrates.enums.Messages;
import br.com.eterniaserver.eterniacrates.objects.CustomizableMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MsgCfg {

    private final String[] messages;

    public MsgCfg() {
        Messages[] economiesID = Messages.values();
        messages = new String[Messages.values().length];

        Map<String, CustomizableMessage> defaults = new HashMap<>();

        this.addDefault(defaults, Messages.CRATE_TITLE, "Mostrando itens dá caixa $3{0}$8.", "0: nome da caixa");
        this.addDefault(defaults, Messages.CRATE_ITENS, "ID $3{0} $8| $7Item$8: $3{1} $8| $7Chance$8: $3{2}$8.", "0: id do item; 1: quantia e nome do item; 2: chance de dropar item");
        this.addDefault(defaults, Messages.CRATE_NOT_FOUND, "Nenhuma caixa com o nome $3{0}$8.", "0: nome da caixa");
        this.addDefault(defaults, Messages.CRATE_KEY_RECEIVED, "Você recebeu $3{0}$7 chaves da caixa $3{1}$8.", "0: quantia de chaves; 1: nome da caixa");
        this.addDefault(defaults, Messages.CRATE_KEY_SEND, "Você enviou $3{0}$7 chaves da caixa $3{1} $7para $3{3}$8.", "0: quantia de chaves; 1: nome da caixa; 2: nome do jogador; 3: apelido do jogador");
        this.addDefault(defaults, Messages.CRATE_ALREADY_EXIST, "A caixa $3{0}$7 já existe$8.", "0: nome da caixa");
        this.addDefault(defaults, Messages.CRATE_CREATED, "Você criou uma caixa com o nome de $3{0}$8.", "0: nome da caixa");
        this.addDefault(defaults, Messages.CRATE_SET_COOLDOWN, "Você definiu o cooldown da caixa $3{0} $7para $3{1}$8.", "0: nome da caixa; 1: cooldown");
        this.addDefault(defaults, Messages.CRATE_LOCATION, "Para definir a localização da caixa $3{0} $7clique com o botão direto em um báu$8.", "0: nome da caixa");
        this.addDefault(defaults, Messages.CRATE_ITEM_PUT, "Você adicionou um item na caixa $3{0}$8.", "0: nome da caixa");
        this.addDefault(defaults, Messages.NO_AIR, "Você precisa está segurando um item$8.", null);
        this.addDefault(defaults, Messages.CRATE_DELETED, "Caixa deletada com sucesso$8.", null);
        this.addDefault(defaults, Messages.CRATE_KEY_SETTED, "Chave definida para a caixa $3{0}$8.", "0: nome da caixa");
        this.addDefault(defaults, Messages.KEY_NBT, "O código NBT da caixa é $3EterniaKey {0}$8.", "0: key da caixa");
        this.addDefault(defaults, Messages.NO_ITEM, "Esse item não existe$8.", null);
        this.addDefault(defaults, Messages.ITEM_REMOVED, "Item removido da caixa$8.", null);
        this.addDefault(defaults, Messages.IN_COOLDOWN, "Você não pode abrir essa caixa no momento$8.", null);
        this.addDefault(defaults, Messages.ITEM_FAIL, "Que pena$8,$7 você não ganhou nada$8.", null);
        this.addDefault(defaults, Messages.ITEM_NO_KEY, "Você não possui uma chave$8.", null);
        this.addDefault(defaults, Messages.ITEM_WINNER, "Parabéns você ganhou o $3{0}$8.", "0: nome do item");
        this.addDefault(defaults, Messages.CRATE_LOC_SET, "Localização da caixa definida$8.", null);

        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Constants.MESSAGES_FILE_PATH));

        for (Messages messagesEnum : economiesID) {
            CustomizableMessage messageData = defaults.get(messagesEnum.name());

            if (messageData == null) {
                messageData = new CustomizableMessage(messagesEnum, EterniaCrates.configs.serverPrefix +"Mensagem faltando para $3" + messagesEnum.name() + "$8.", null);
                APIServer.logError("Entrada para a mensagem " + messagesEnum.name(), 2);
            }

            this.messages[messagesEnum.ordinal()] = config.getString(messagesEnum.name() + ".text", messageData.text);
            config.set(messagesEnum.name() + ".text", this.messages[messagesEnum.ordinal()]);

            this.messages[messagesEnum.ordinal()] = this.messages[messagesEnum.ordinal()].replace('$', (char) 0x00A7);

            if (messageData.getNotes() != null) {
                messageData.setNotes(config.getString(messagesEnum.name() + ".notes", messageData.getNotes()));
                config.set(messagesEnum.name() + ".notes", messageData.getNotes());
            }

        }

        if (new File(Constants.DATA_LOCALE_FOLDER_PATH).mkdir()) {
            APIServer.logError("Pasta de locales criada com sucesso", 1);
        }

        try {
            config.save(Constants.MESSAGES_FILE_PATH);
        } catch (IOException exception) {
            APIServer.logError("Impossível de criar arquivos em " + Constants.DATA_LOCALE_FOLDER_PATH, 3);
        }

        defaults.clear();
    }

    private void addDefault(Map<String, CustomizableMessage> defaults, Messages id, String text, String notes) {
        CustomizableMessage message = new CustomizableMessage(id, text, notes);
        defaults.put(id.name(), message);
    }

    public void sendMessage(CommandSender player, Messages messagesId, String... args) {
        sendMessage(player, messagesId, true, args);
    }

    public void sendMessage(CommandSender player, Messages messagesId, boolean prefix, String... args) {
        player.sendMessage(getMessage(messagesId, prefix, args));
    }

    public String getMessage(Messages messagesId, boolean prefix, String... args) {
        String message = messages[messagesId.ordinal()];

        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            message = message.replace("{" + i + "}", param);
        }

        if (prefix) {
            return EterniaCrates.configs.serverPrefix + message;
        }

        return message;
    }


}
