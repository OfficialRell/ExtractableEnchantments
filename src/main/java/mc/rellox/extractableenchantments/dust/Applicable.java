package mc.rellox.extractableenchantments.dust;

import java.util.Set;

import mc.rellox.extractableenchantments.api.dust.IApplicable;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;

public record Applicable(Set<IExtractor> extractors, boolean books) implements IApplicable {}