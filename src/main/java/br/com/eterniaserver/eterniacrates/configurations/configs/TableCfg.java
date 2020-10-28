package br.com.eterniaserver.eterniacrates.configurations.configs;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.EterniaLib;

public class TableCfg {

    public TableCfg() {

        final String PRIMARY_KEY = "(id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, ";

        if (EterniaLib.getMySQL()) {
            EQueries.executeQuery(Constants.getQueryCreateTable(EterniaCrates.configs.tableUsersCooldown,
                    PRIMARY_KEY + "uuid VARCHAR(53), " +
                            "cooldown BIGINT(20))"), false);
            EQueries.executeQuery(Constants.getQueryCreateTable(EterniaCrates.configs.tableCrates,
                    PRIMARY_KEY +
                            "crate VARCHAR(16), " +
                            "cooldown BIGINT(20), " +
                            "cratekey MEDIUMBLOB, " +
                            "location VARCHAR(64))"), false);
            EQueries.executeQuery(Constants.getQueryCreateTable(EterniaCrates.configs.tableItens,
                    PRIMARY_KEY +
                            "crate VARCHAR(16), " +
                            "item MEDIUMBLOB, " +
                            "chance DOUBLE)"), false);
        } else {
            EQueries.executeQuery(Constants.getQueryCreateTable(EterniaCrates.configs.tableUsersCooldown,
                    "(uuid VARCHAR(36), " +
                            "cooldown INTEGER)"), false);
            EQueries.executeQuery(Constants.getQueryCreateTable(EterniaCrates.configs.tableCrates,
                    "(crate VARCHAR(16), " +
                            "cratekey MEDIUMBLOB, " +
                            "cooldown INTEGER, " +
                            "location VARCHAR(64))"), false);
            EQueries.executeQuery(Constants.getQueryCreateTable(EterniaCrates.configs.tableItens,
                    "(crate VARCHAR(16), " +
                            "item MEDIUMBLOB, " +
                            "chance DOUBLE)"), false);
        }

    }

}
