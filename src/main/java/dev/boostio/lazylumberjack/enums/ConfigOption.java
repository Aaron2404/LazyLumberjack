package dev.boostio.lazylumberjack.enums;

import lombok.Getter;

@Getter
public enum ConfigOption {
    DETECTION_LEAF_RANGE("detection.leaf-range", 2),
    DETECTION_AIR_RANGE("detection.air-range", 1),

    SLOW_BREAK_ENABLED("animations.slow-break.enabled", true),

    SLOW_BREAK_BASE_DELAY("animations.slow-break.delay.base-delay", 40),
    SLOW_BREAK_SPEED_FACTOR("animations.slow-break.delay.speed-factor", 0.1),

    PARTICLES_ENABLED("animations.slow-break.particles.enabled", true),
    PARTICLES_AMOUNT("animations.slow-break.particles.amount", 5),
    PARTICLES_OFFSET_X("animations.slow-break.particles.offset.x", 0.0),
    PARTICLES_OFFSET_Y("animations.slow-break.particles.offset.y", 0.0),
    PARTICLES_OFFSET_Z("animations.slow-break.particles.offset.z", 0.0),

    SAPLING_PLANTING_ENABLED("helpers.place-sapling", true);

    private final String key;
    private final Object defaultValue;

    <T> ConfigOption(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public <T> T getDefaultValue() {
        return (T) defaultValue;
    }
}