package de.variamc.velocitysystem.manager;

import de.variamc.velocitysystem.manager.config.Config;

/**
 * Class created by Kaseax on 2022
 */
public class ConfigManager {

    private final Config config;

    public ConfigManager() {
        this.config = new Config();
    }

    public Config getConfig() {
        return config;
    }
}
