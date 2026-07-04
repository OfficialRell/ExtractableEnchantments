package mc.rellox.extractableenchantments.dust;

import mc.rellox.extractableenchantments.api.dust.IApplicable;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;

import java.util.Set;

public record Applicable(
        Set<IExtractor> extractors,
        boolean books
) implements IApplicable {}