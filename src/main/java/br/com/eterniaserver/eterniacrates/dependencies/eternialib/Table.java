package br.com.eterniaserver.eterniacrates.dependencies.eternialib;

import br.com.eterniaserver.eterniacrates.generics.PluginConfigs;
import br.com.eterniaserver.eterniacrates.generics.PluginConstants;
import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.EterniaLib;

public class Table {

    public Table() {

        final String PRIMARY_KEY = "(id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, ";

        if (EterniaLib.getMySQL()) {
            EQueries.executeQuery(PluginConstants.getQueryCreateTable(PluginConfigs.TABLE_USERS,
                    PRIMARY_KEY + "uuid VARCHAR(53), " +
                            "cooldown BIGINT(20))"), false);
            EQueries.executeQuery(PluginConstants.getQueryCreateTable(PluginConfigs.TABLE_CRATES,
                    PRIMARY_KEY +
                            "crate VARCHAR(16), " +
                            "cooldown BIGINT(20), " +
                            "cratekey MEDIUMBLOB, " +
                            "location VARCHAR(64))"), false);
            EQueries.executeQuery(PluginConstants.getQueryCreateTable(PluginConfigs.TABLE_ITENS,
                    PRIMARY_KEY +
                            "crate VARCHAR(16), " +
                            "item MEDIUMBLOB, " +
                            "chance FLOAT)"), false);
        } else {
            EQueries.executeQuery(PluginConstants.getQueryCreateTable(PluginConfigs.TABLE_USERS,
                    "(uuid VARCHAR(36), " +
                            "cooldown INTEGER)"), false);
            EQueries.executeQuery(PluginConstants.getQueryCreateTable(PluginConfigs.TABLE_CRATES,
                    "(crate VARCHAR(16), " +
                            "cratekey MEDIUMBLOB, " +
                            "cooldown INTEGER, " +
                            "location VARCHAR(64))"), false);
            EQueries.executeQuery(PluginConstants.getQueryCreateTable(PluginConfigs.TABLE_ITENS,
                    "(crate VARCHAR(16), " +
                            "item MEDIUMBLOB, " +
                            "chance FLOAT)"), false);
        }

    }

}
