package br.com.eterniaserver.eterniacrates;

import br.com.eterniaserver.acf.ConditionFailedException;
import br.com.eterniaserver.eterniacrates.commands.Crate;
import br.com.eterniaserver.eterniacrates.enums.Commands;
import br.com.eterniaserver.eternialib.EterniaLib;

public class Managers {

    public Managers() {
        loadCommandsLocale();
        loadConditions();
        registerCommands();
    }

    private void loadCommandsLocale() {
        EterniaLib.getManager().getCommandReplacements().addReplacements(
                "crate", EterniaCrates.commands.getName(Commands.CRATE),
                "crate_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE),
                "crate_description", EterniaCrates.commands.getDescription(Commands.CRATE),
                "crate_perm", EterniaCrates.commands.getPerm(Commands.CRATE),
                "crate_key", EterniaCrates.commands.getName(Commands.CRATE_KEY),
                "crate_key_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_KEY),
                "crate_key_description", EterniaCrates.commands.getDescription(Commands.CRATE_KEY),
                "crate_key_perm", EterniaCrates.commands.getPerm(Commands.CRATE_KEY),
                "crate_create", EterniaCrates.commands.getName(Commands.CRATE_CREATE),
                "crate_create_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_CREATE),
                "crate_create_description", EterniaCrates.commands.getDescription(Commands.CRATE_CREATE),
                "crate_create_perm", EterniaCrates.commands.getPerm(Commands.CRATE_CREATE),
                "crate_cooldown", EterniaCrates.commands.getName(Commands.CRATE_COOLDOWN),
                "crate_cooldown_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_COOLDOWN),
                "crate_cooldown_description", EterniaCrates.commands.getDescription(Commands.CRATE_COOLDOWN),
                "crate_cooldown_perm", EterniaCrates.commands.getPerm(Commands.CRATE_COOLDOWN),
                "crate_location", EterniaCrates.commands.getName(Commands.CRATE_LOCATION),
                "crate_location_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_LOCATION),
                "crate_location_description", EterniaCrates.commands.getDescription(Commands.CRATE_LOCATION),
                "crate_location_perm", EterniaCrates.commands.getPerm(Commands.CRATE_LOCATION),
                "crate_putitem", EterniaCrates.commands.getName(Commands.CRATE_PUT_ITEM),
                "crate_putitem_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_PUT_ITEM),
                "crate_putitem_description", EterniaCrates.commands.getDescription(Commands.CRATE_PUT_ITEM),
                "crate_putitem_perm", EterniaCrates.commands.getPerm(Commands.CRATE_PUT_ITEM),
                "crate_listitens", EterniaCrates.commands.getName(Commands.CRATE_LISTITENS),
                "crate_listitens_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_LISTITENS),
                "crate_listitens_description", EterniaCrates.commands.getDescription(Commands.CRATE_LISTITENS),
                "crate_listitens_perm", EterniaCrates.commands.getPerm(Commands.CRATE_LISTITENS),
                "crate_delete", EterniaCrates.commands.getName(Commands.CRATE_DELETE),
                "crate_delete_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_DELETE),
                "crate_delete_description", EterniaCrates.commands.getDescription(Commands.CRATE_DELETE),
                "crate_delete_perm", EterniaCrates.commands.getPerm(Commands.CRATE_DELETE),
                "crate_setkey", EterniaCrates.commands.getName(Commands.CRATE_SETKEY),
                "crate_setkey_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_SETKEY),
                "crate_setkey_description", EterniaCrates.commands.getDescription(Commands.CRATE_SETKEY),
                "crate_setkey_perm", EterniaCrates.commands.getPerm(Commands.CRATE_SETKEY),
                "crate_nbt", EterniaCrates.commands.getName(Commands.CRATE_NBT),
                "crate_nbt_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_NBT),
                "crate_nbt_description", EterniaCrates.commands.getDescription(Commands.CRATE_NBT),
                "crate_nbt_perm", EterniaCrates.commands.getPerm(Commands.CRATE_NBT),
                "crate_givekeyall", EterniaCrates.commands.getName(Commands.CRATE_GIVEKEYALL),
                "crate_givekeyall_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_GIVEKEYALL),
                "crate_givekeyall_description", EterniaCrates.commands.getDescription(Commands.CRATE_GIVEKEYALL),
                "crate_givekeyall_perm", EterniaCrates.commands.getPerm(Commands.CRATE_GIVEKEYALL),
                "crate_removeitem", EterniaCrates.commands.getName(Commands.CRATE_REMOVEITEM),
                "crate_removeitem_syntax", EterniaCrates.commands.getSyntax(Commands.CRATE_REMOVEITEM),
                "crate_removeitem_description", EterniaCrates.commands.getDescription(Commands.CRATE_REMOVEITEM),
                "crate_removeitem_perm", EterniaCrates.commands.getPerm(Commands.CRATE_REMOVEITEM)
        );
    }

    private void loadConditions() {
        EterniaLib.getManager().getCommandConditions().addCondition(Double.class, "limits", (c, exec, value) -> {
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

    private void registerCommands() {
        EterniaLib.getManager().registerCommand(new Crate());
    }

}
