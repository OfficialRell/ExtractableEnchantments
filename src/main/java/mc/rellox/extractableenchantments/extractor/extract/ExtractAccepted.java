package mc.rellox.extractableenchantments.extractor.extract;

import mc.rellox.extractableenchantments.api.extractor.extract.IAccepted;
import mc.rellox.extractableenchantments.api.extractor.extract.IIgnoredEnchantment;

import java.util.Set;

public record ExtractAccepted(
        Set<IIgnoredEnchantment> ignored,
        boolean invert
) implements IAccepted {}