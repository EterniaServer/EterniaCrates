package br.com.eterniaserver.eterniacrates.generics;

import br.com.eterniaserver.eterniacrates.EterniaCrates;

public class PluginConfigs {

    private PluginConfigs() {
        throw new IllegalStateException("Utility class");
    }

    public static final String TABLE_USERS = EterniaCrates.serverConfig.getString("sql.table-cooldown");
    public static final String TABLE_ITENS = EterniaCrates.serverConfig.getString("sql.table-itens");
    public static final String TABLE_CRATES = EterniaCrates.serverConfig.getString("sql.table-crates");

}
