package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eterniacrates.objects.CrateData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class APIServer {

    private APIServer() {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<String, CrateData> crateMap = new HashMap<>();
    private static final Map<String, Long> userCooldownMap = new HashMap<>();
    private static final Map<UUID, String> locCached = new HashMap<>();

    public static boolean existsCrate(String crateName) {
        return crateMap.containsKey(crateName);
    }

    public static CrateData getCrate(String crateName) {
        return crateMap.get(crateName);
    }

    public static void createCrate(String crateName) {
        crateMap.put(crateName, new CrateData(crateName));
    }

    public static void putCrate(String crateName, CrateData crateData) {
        crateMap.put(crateName, crateData);
    }

    public static void removeCrate(String crateName) {
        crateMap.remove(crateName);
    }

    public static void putUserCooldown(String str, long amount) {
        userCooldownMap.put(str, amount);
    }

    public static long getUserCooldown(String str) {
        return userCooldownMap.getOrDefault(str, System.currentTimeMillis());
    }

    public static boolean hasUserCooldown(String str) {
        return userCooldownMap.containsKey(str);
    }

    public static void putCachedLoc(UUID uuid, String crateName) {
        locCached.put(uuid, crateName);
    }

    public static boolean hasCachedLoc(UUID uuid) {
        return locCached.containsKey(uuid);
    }

    public static String getCachedLoc(UUID uuid) {
        return locCached.get(uuid);
    }

    public static void removeCachedLoc(UUID uuid) {
        locCached.remove(uuid);
    }

    public static void logError(String errorMsg, int level) {
        String errorLevel;
        switch (level) {
            case 1:
                errorLevel = ChatColor.GREEN + "Leve";
                break;
            case 2:
                errorLevel = ChatColor.YELLOW + "Aviso";
                break;
            default:
                errorLevel = ChatColor.RED + "Erro";
        }
        Bukkit.getConsoleSender().sendMessage(("$8[$aE$9S$8] " + errorLevel + "$8:$3" + errorMsg + "$8.").replace('$', (char) 0x00A7));
    }

}
