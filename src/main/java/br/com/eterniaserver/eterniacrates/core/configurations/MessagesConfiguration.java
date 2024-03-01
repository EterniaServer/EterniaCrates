package br.com.eterniaserver.eterniacrates.core.configurations;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.core.enums.Messages;
import br.com.eterniaserver.eternialib.chat.MessageMap;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.configuration.interfaces.MsgConfiguration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessagesConfiguration implements MsgConfiguration<Messages> {

    private final FileConfiguration inFile;
    private final FileConfiguration outFile;

    private final MessageMap<Messages, String> messageMap = new MessageMap<>(Messages.class, Messages.CRATES_PREFIX);

    public MessagesConfiguration() {
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
        return Constants.MESSAGES_FILE_PATH;
    }

    @Override
    public MessageMap<Messages, String> messages() {
        return messageMap;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.GENERIC;
    }

    @Override
    public void executeConfig() {
        addMessage(Messages.CRATES_PREFIX,
                "#555555[#34eb40E#3471ebC#555555]#AAAAAA "
        );
        addMessage(Messages.CRATE_TITLE,
                "Mostrando itens dá caixa #00aaaa{0}#555555.",
                "nome da caixa"
        );
        addMessage(Messages.CRATE_ITENS,
                "ID #00aaaa{0}#555555 | #AAAAAAItem#555555: #00aaaa{1}#555555 | #AAAAAAChance#555555: #00aaaa{2}#555555.",
                "id do item",
                "quantia e nome do item",
                "chance de dropar o item"
        );
        addMessage(Messages.CRATE_NOT_FOUND,
                "Nenhuma caixa com o nome #00aaaa{0}#AAAAAA #555555.",
                "nome do item"
        );
        addMessage(Messages.CRATE_KEY_RECEIVED,
                "Você recebeu #00aaaa{0}#AAAAAA chaves da caixa #00aaaa{1}#555555.",
                "quantia de chaves",
                "nome da caixa"
        );
        addMessage(Messages.CRATE_KEY_SEND,
                "Você enviou #00aaaa{0}#AAAAAA chaves da caixa #00aaaa{1}#AAAAAA para #00aaaa{3}#AAAAAA.",
                "quantia de chaves",
                "nome da caixa",
                "nome do jogador",
                "apelido do jogador"
        );
        addMessage(Messages.CRATE_ALREADY_EXIST,
                "A caixa #00aaaa{0}#AAAAAA já existe#555555.",
                "nome da caixa"
        );
        addMessage(Messages.CRATE_CREATED,
                "Você criou uma caixa com o nome de #00aaaa{0}#555555.",
                "nome da caixa"
        );
        addMessage(Messages.CRATE_SET_COOLDOWN,
                "Você definiu o cooldown da caixa #00aaaa{0}#AAAAAA para #00aaaa{1}#555555.",
                "nome da caixa",
                "cooldown"
        );
        addMessage(Messages.CRATE_LOCATION,
                "Para definir a localização da caixa #00aaaa{0}#AAAAAA clique com o botão direto em um báu#555555.",
                "nome da caixa"
        );
        addMessage(Messages.CRATE_ITEM_PUT,
                "Você adicionou um item na caixa #00aaaa{0}#555555.",
                "nome da caixa"
        );
        addMessage(Messages.NO_AIR,
                "Você precisa está segurando um item#555555."
        );
        addMessage(Messages.CRATE_DELETED,
                "Caixa deletada com sucesso#555555."
        );
        addMessage(Messages.CRATE_KEY_SETTED,
                "Chave definida para a caixa #00aaaa{0}#555555.",
                "nome da caixa"
        );
        addMessage(Messages.KEY_NBT,
                "O código NBT da caixa é #00aaaaEterniaKey {0}#555555.",
                "key da caixa"
        );
        addMessage(Messages.NO_ITEM,
                "Esse item não existe#555555."
        );
        addMessage(Messages.ITEM_REMOVED,
                "Item removido da caixa#555555."
        );
        addMessage(Messages.IN_COOLDOWN,
                "Você não pode abrir essa caixa no momento#555555."
        );
        addMessage(Messages.ITEM_FAIL,
                "Que pena#555555, você não ganhou nada#555555."
        );
        addMessage(Messages.ITEM_NO_KEY,
                "Você não possui uma chave#555555."
        );
        addMessage(Messages.ITEM_WINNER,
                "Parabéns você ganhou o #00aaaa{0}#555555.",
                "nome do item"
        );
        addMessage(Messages.CRATE_LOC_SET,
                "Localização da caixa definida#555555."
        );
    }

    @Override
    public void executeCritical() { }

}
