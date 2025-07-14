package mc.rellox.extractableenchantments.extractor.extract;

import java.util.Set;

import mc.rellox.extractableenchantments.api.extractor.extract.IAccepted;
import mc.rellox.extractableenchantments.api.extractor.extract.IIgnoredEnchantment;

public record ExtractAccepted(Set<IIgnoredEnchantment> ignored, boolean invert) implements IAccepted {}