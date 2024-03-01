package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.ReferenceField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

public class Entities {

    private Entities() {
        throw new IllegalStateException("Utility class");
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(tableName = "%e_users_cooldown%")
    public static class UserCooldown {

        @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
        private Integer id;

        @DataField(columnName = "uuid", type = FieldType.UUID, notNull = true)
        private UUID uuid;

        @DataField(columnName = "crateName", type = FieldType.STRING, notNull = true)
        private String crateName;

        @DataField(columnName = "cooldown", type = FieldType.TIMESTAMP, notNull = true)
        private Timestamp cooldown;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(tableName = "%e_crates%")
    public static class Crate {

        @PrimaryKeyField(columnName = "crate", type = FieldType.STRING, autoIncrement = false)
        private String crate;

        @DataField(columnName = "cooldown", type = FieldType.INTEGER)
        private Integer cooldown;

        @DataField(columnName = "crateKey", type = FieldType.BLOB)
        private byte[] crateKey;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(tableName = "%e_crates_items%")
    public static class CrateItem {

        @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
        private Integer id;

        @ReferenceField(columnName = "crateName",
                        referenceTableName = "%e_crates%",
                        referenceColumnName = "crate",
                        notNull = true,
                        mode = ReferenceMode.CASCADE)
        @DataField(columnName = "crateName", type = FieldType.STRING, notNull = true)
        private String crateName;

        @DataField(columnName = "item", type = FieldType.BLOB, notNull = true)
        private byte[] item;

        @DataField(columnName = "chance", type = FieldType.DOUBLE, notNull = true)
        private Double chance;

    }

}
