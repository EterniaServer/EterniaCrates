package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eterniacrates.objects.CrateData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public interface APIServer {

    static boolean existsCrate(String crateName) {
        return Vars.crateMap.containsKey(crateName);
    }

    static CrateData getCrate(String crateName) {
        return Vars.crateMap.get(crateName);
    }

    static void createCrate(String crateName) {
        Vars.crateMap.put(crateName, new CrateData(crateName));
    }

    static void putCrate(String crateName, CrateData crateData) {
        Vars.crateMap.put(crateName, crateData);
    }

    static void removeCrate(String crateName) {
        Vars.crateMap.remove(crateName);
    }

    static void putUserCooldown(String str, long amount) {
        Vars.userCooldownMap.put(str, amount);
    }

    static long getUserCooldown(String str) {
        return Vars.userCooldownMap.getOrDefault(str, System.currentTimeMillis());
    }

    static boolean hasUserCooldown(String str) {
        return Vars.userCooldownMap.containsKey(str);
    }

    static void putCachedLoc(UUID uuid, String crateName) {
        Vars.locCached.put(uuid, crateName);
    }

    static boolean hasCachedLoc(UUID uuid) {
        return Vars.locCached.containsKey(uuid);
    }

    static String getCachedLoc(UUID uuid) {
        return Vars.locCached.get(uuid);
    }

    static void removeCachedLoc(UUID uuid) {
        Vars.locCached.remove(uuid);
    }

    static void logError(String errorMsg, int level) {
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
