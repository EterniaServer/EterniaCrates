package br.com.eterniaserver.eterniacrates.generics;

import br.com.eterniaserver.eterniacrates.EterniaCrates;

import org.bukkit.ChatColor;

public class PluginMSGs {

    private PluginMSGs() {
        throw new IllegalStateException("Utility class");
    }

    public static final String M_SERVER_PREFIX = getColor(EterniaCrates.msgConfig.getString("server.prefix"));
    public static final String ITEM_WINNER = putPrefix("crates.winner");
    public static final String ITEM_FAIL = putPrefix("crates.item-fail");
    public static final String COOLDOWN = putPrefix("crates.cooldown");
    public static final String CREATE = putPrefix("crates.create");
    public static final String ALREADY = putPrefix("crates.already");
    public static final String NO_EXISTS = putPrefix("crates.no-exists");
    public static final String SET_LOC = putPrefix("crates.set-loc");
    public static final String LOC_SETED = putPrefix("crates.loc-seted");
    public static final String ITEM_ADD = putPrefix("crates.item-add");
    public static final String KEY_SET = putPrefix("crates.key-set");
    public static final String COOLDOWN_SET = putPrefix("crates.cooldown-set");
    public static final String NO_KEY = putPrefix("crates.no-key");
    public static final String KEY_GIVE = putPrefix("crates.key-give");
    public static final String KEY_RECEIVE = putPrefix("crates.key-receive");

    private static String putPrefix(String path) {
        String message = EterniaCrates.msgConfig.getString(path);
        if (message == null) message = "&7Erro&8, &7texto &3" + path + "&7não encontrado&8.";
        return M_SERVER_PREFIX + getColor(message);
    }

    public static String getColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
