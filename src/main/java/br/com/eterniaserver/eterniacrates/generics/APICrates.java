package br.com.eterniaserver.eterniacrates.generics;

import br.com.eterniaserver.eterniacrates.objects.CratesData;

public class APICrates {

    public static boolean existsCrate(String crateName) {
        return PluginVars.cratesNameMap.containsKey(crateName);
    }

    public static CratesData getCrate(String crateName) {
        return PluginVars.cratesNameMap.get(crateName);
    }

    public static void createCrate(String crateName) {
        PluginVars.cratesNameMap.put(crateName, new CratesData(crateName));
    }

}
