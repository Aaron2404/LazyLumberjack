package dev.boostio.lazylumberjack.managers;

import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.enums.ConfigOption;

import java.util.EnumMap;
import java.util.Map;

public class ConfigManager {
    private final LazyLumberjack plugin;
    private final Map<ConfigOption, Object> configurationOptions = new EnumMap<>(ConfigOption.class);

    public ConfigManager(LazyLumberjack plugin) {
        this.plugin = plugin;

        saveDefaultConfiguration();
        loadConfigurationOptions();
    }

    private void saveDefaultConfiguration() {
        plugin.saveDefaultConfig();
    }

    private void loadConfigurationOptions() {
        for (ConfigOption option : ConfigOption.values()) {
            configurationOptions.put(option, plugin.getConfig().get(option.getKey(), option.getDefaultValue()));
        }
    }

    public <T> T getConfigurationOption(ConfigOption option) {
        return (T) configurationOptions.get(option);
    }

    public boolean getBoolean(ConfigOption option) {
        return (boolean) configurationOptions.get(option);
    }
}