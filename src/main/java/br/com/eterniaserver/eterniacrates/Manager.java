package br.com.eterniaserver.eterniacrates;

import br.com.eterniaserver.acf.ConditionFailedException;
import br.com.eterniaserver.eterniacrates.core.configurations.CommandConfiguration;
import br.com.eterniaserver.eterniacrates.core.configurations.CrateConfiguration;
import br.com.eterniaserver.eterniacrates.core.configurations.MessagesConfiguration;
import br.com.eterniaserver.eterniacrates.core.CrateCommands;
import br.com.eterniaserver.eterniacrates.core.CrateImp;
import br.com.eterniaserver.eterniacrates.core.Entities.Crate;
import br.com.eterniaserver.eterniacrates.core.Entities.CrateItem;
import br.com.eterniaserver.eterniacrates.core.Entities.UserCooldown;
import br.com.eterniaserver.eterniacrates.core.enums.Strings;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.database.Entity;

import java.util.List;

public class Manager {

    public Manager(EterniaCrates plugin) {
        CrateImp crateImp = new CrateImp(plugin);

        CrateConfiguration crateConfiguration = new CrateConfiguration(plugin);
        MessagesConfiguration messagesConfiguration = new MessagesConfiguration();
        CommandConfiguration commandConfiguration = new CommandConfiguration();

        EterniaLib.getCfgManager().registerConfiguration("eterniacrates", "core", true, crateConfiguration);
        EterniaLib.getCfgManager().registerConfiguration("eterniacrates", "messages", true, messagesConfiguration);
        EterniaLib.getCfgManager().registerConfiguration("eterniacrates", "commands", true, commandConfiguration);

        try {
            Entity<UserCooldown> userCooldownEntity = new Entity<>(UserCooldown.class);
            Entity<Crate> cratesEntity = new Entity<>(Crate.class);
            Entity<CrateItem> cratesItemsEntity = new Entity<>(CrateItem.class);

            EterniaLib.getDatabase().addTableName("%e_users_cooldown%", plugin.getString(Strings.TABLE_USERS_COOLDOWN));
            EterniaLib.getDatabase().addTableName("%e_crates%", plugin.getString(Strings.TABLE_CRATES));
            EterniaLib.getDatabase().addTableName("%e_crates_items%", plugin.getString(Strings.TABLE_ITEMS));

            EterniaLib.getDatabase().register(UserCooldown.class, userCooldownEntity);
            EterniaLib.getDatabase().register(Crate.class, cratesEntity);
            EterniaLib.getDatabase().register(CrateItem.class, cratesItemsEntity);
        }
        catch (Exception exception) {
            plugin.getLogger().warning(exception.getMessage());
            return;
        }

        List<UserCooldown> userCooldowns = EterniaLib.getDatabase().listAll(UserCooldown.class);
        List<Crate> crates = EterniaLib.getDatabase().listAll(Crate.class);
        List<CrateItem> crateItems = EterniaLib.getDatabase().listAll(CrateItem.class);

        crateImp.putCrates(crates);
        crateImp.putCratesItems(crateItems);
        crateImp.putUserCooldowns(userCooldowns);

        EterniaCrates.setCrateAPI(crateImp);

        loadConditions();
        loadCompletions();
        registerCommands(plugin);
    }

    private void loadConditions() {
        EterniaLib.getCmdManager().getCommandConditions().addCondition(Double.class, "limits", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (c.getConfigValue("min", 0) > value) {
                throw new ConditionFailedException("O valor mínimo precisa ser &3" + c.getConfigValue("min", 0));
            }
            if (c.getConfigValue("max", 3) < value) {
                throw new ConditionFailedException("O valor máximo precisa ser &3 " + c.getConfigValue("max", 3));
            }
        });
    }

    private void loadCompletions() {
        EterniaLib.getCmdManager().getCommandCompletions().registerStaticCompletion("crates", EterniaCrates.getCrateAPI().crateNames());
    }

    private void registerCommands(EterniaCrates plugin) {
        EterniaLib.getCmdManager().registerCommand(new CrateCommands(plugin));
    }

}
