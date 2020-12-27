package br.com.eterniaserver.eterniacrates.configurations.configs;

import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.SQL;
import br.com.eterniaserver.eternialib.sql.queries.CreateTable;

public class TableCfg {

    public TableCfg() {
        CreateTable createTable;
        if (EterniaLib.getMySQL()) {
            createTable = new CreateTable(EterniaCrates.configs.tableUsersCooldown);
            createTable.columns.set("id INT AUTO_INCREMENT NOT NULL PRIMARY KEY", "uuid VARCHAR(53)", "cooldown BIGINT(20)");
            SQL.execute(createTable);

            createTable = new CreateTable(EterniaCrates.configs.tableCrates);
            createTable.columns.set("id INT AUTO_INCREMENT NOT NULL PRIMARY KEY", "crate VARCHAR(16)", "cooldown BIGINT(20)",
                    "cratekey BLOB", "location VARCHAR(64)");
            SQL.execute(createTable);

            createTable = new CreateTable(EterniaCrates.configs.tableItens);
            createTable.columns.set("id INT AUTO_INCREMENT NOT NULL PRIMARY KEY", "crate VARCHAR(16)", "item BLOB", "chance DOUBLE");
        } else {
            createTable = new CreateTable(EterniaCrates.configs.tableUsersCooldown);
            createTable.columns.set("uuid VARCHAR(36)", "cooldown INTEGER");
            SQL.execute(createTable);

            createTable = new CreateTable(EterniaCrates.configs.tableCrates);
            createTable.columns.set("crate VARCHAR(16)", "cratekey BLOB", "cooldown INTEGER", "location VARCHAR(64)");
            SQL.execute(createTable);

            createTable = new CreateTable(EterniaCrates.configs.tableItens);
            createTable.columns.set("crate VARCHAR(16)", "item BLOB", "chance DOUBLE");
            SQL.execute(createTable);
        }

    }

}
