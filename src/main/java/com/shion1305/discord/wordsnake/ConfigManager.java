package com.shion1305.discord.wordsnake;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

class ConfigManager {
    private static final Logger logger = Logger.getLogger("ConfigManager");
    private static Properties config;
    private final static String configDir = System.getProperty("user.home") + "/ShionServerConfig/WordSnakeDiscordJ/Wordsnake.properties";

    static String getConfig(String field) {
        if (config == null) {
            config = new Properties();
            try (FileInputStream s = new FileInputStream(configDir)) {
                logger.info("Configuration is Loaded");
                config.load(s);
            } catch (IOException e) {
                logger.severe("Configuration LOAD FAILED");
                e.printStackTrace();
            }
        }
        return config.getProperty(field);
    }
}
