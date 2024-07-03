package mc.rellox.extractableenchantments.extractor.extract;

import java.util.Set;

import mc.rellox.extractableenchantments.api.extractor.extract.IAccepted;

public record ExtractAccepted(Set<String> enchantments, boolean invert) implements IAccepted {}