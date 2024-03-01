package br.com.eterniaserver.eterniacrates;

import java.io.File;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String DATA_LAYER_FOLDER_PATH = "plugins" + File.separator + "EterniaCrate";

    public static final String DATA_LOCALE_FOLDER_PATH = Constants.DATA_LAYER_FOLDER_PATH + File.separator + "locales";
    public static final String CONFIG_FILE_PATH = Constants.DATA_LAYER_FOLDER_PATH + File.separator + "config.yml";
    public static final String MESSAGES_FILE_PATH = DATA_LOCALE_FOLDER_PATH + File.separator + "messages.yml";
    public static final String COMMANDS_FILE_PATH = DATA_LOCALE_FOLDER_PATH + File.separator + "commands.yml";

    public static final String ETERNIA_CRATE = "eternia_crate";
}
