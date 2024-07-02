package mc.rellox.extractableenchantments.extractor;

import mc.rellox.extractableenchantments.api.extractor.IChanceOverride;

public record ChanceOverride(boolean enabled, int value) implements IChanceOverride {}
