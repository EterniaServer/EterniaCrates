package br.com.eterniaserver.eterniacrates.generics;

import br.com.eterniaserver.eterniacrates.objects.CratesData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PluginVars {

    protected static Map<String, CratesData> cratesNameMap = new HashMap<>();
    protected static Map<String, Long> usersCooldown = new HashMap<>();
    protected static Map<String, CratesData> cratesDataMap = new HashMap<>();
    protected static Map<UUID, String> cacheSetLoc = new HashMap<>();

}
