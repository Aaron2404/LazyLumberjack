/*
 * This file is part of LazyLumberjack - https://github.com/Aaron2404/LazyLumberjack
 * Copyright (C) 2024 Aaron and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.boostio.lazylumberjack.managers;

import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.data.Settings;
import lombok.Getter;

public class ConfigManager {
    private final LazyLumberjack plugin;

    @Getter
    private Settings settings;

    public ConfigManager(LazyLumberjack plugin) {
        this.plugin = plugin;

        saveDefaultConfiguration();
        loadConfigurationOptions();
    }

    private void saveDefaultConfiguration() {
        plugin.saveDefaultConfig();
    }

    private void loadConfigurationOptions() {
        Settings settings = new Settings();

        settings.getDetection().setLeafRange(plugin.getConfig().getInt("detection.leaf-range", 2));
        settings.getDetection().setAirRange(plugin.getConfig().getInt("detection.air-range", 1));

        settings.getAnimations().getSlowBreak().setEnabled(plugin.getConfig().getBoolean("animations.slow-break.enabled", true));
        settings.getAnimations().getSlowBreak().getParticles().setEnabled(plugin.getConfig().getBoolean("animations.slow-break.particles.enabled", true));
        settings.getAnimations().getSlowBreak().getParticles().setAmount(plugin.getConfig().getInt("animations.slow-break.particles.amount", 5));
        settings.getAnimations().getSlowBreak().getParticles().getOffset().setX((float) plugin.getConfig().getDouble("animations.slow-break.particles.offset.x", 0.0f));
        settings.getAnimations().getSlowBreak().getParticles().getOffset().setY((float) plugin.getConfig().getDouble("animations.slow-break.particles.offset.y", 0.0f));
        settings.getAnimations().getSlowBreak().getParticles().getOffset().setZ((float) plugin.getConfig().getDouble("animations.slow-break.particles.offset.z", 0.0f));
        settings.getAnimations().getSlowBreak().getDelay().setBaseDelay(plugin.getConfig().getInt("animations.slow-break.delay.base-delay", 40));
        settings.getAnimations().getSlowBreak().getDelay().setSpeedFactor(plugin.getConfig().getDouble("animations.slow-break.delay.speed-factor", 0.1));

        settings.getHelpers().setPlaceSapling(plugin.getConfig().getBoolean("helpers.place-sapling", true));

        this.settings = settings;
    }
}