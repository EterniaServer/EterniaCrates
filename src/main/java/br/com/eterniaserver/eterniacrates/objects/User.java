package br.com.eterniaserver.eterniacrates.objects;

import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.enums.Messages;
import br.com.eterniaserver.eternialib.UUIDFetcher;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    private final Player player;
    private final UUID uuid;

    public User(Player player) {
        this.player = player;
        this.uuid = UUIDFetcher.getUUIDOf(player.getName());
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void sendMessage(Messages message, String... args) {
        EterniaCrates.msg.sendMessage(player, message, args);
    }

    public void sendComponent(TextComponent textComponent) {
        player.sendMessage(textComponent);
    }

}
