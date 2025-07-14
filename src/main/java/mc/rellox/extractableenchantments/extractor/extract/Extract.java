package mc.rellox.extractableenchantments.extractor.extract;

import mc.rellox.extractableenchantments.api.extractor.extract.ExtractFilter;
import mc.rellox.extractableenchantments.api.extractor.extract.ExtractType;
import mc.rellox.extractableenchantments.api.extractor.extract.IAccepted;
import mc.rellox.extractableenchantments.api.extractor.extract.IExtract;

public record Extract(boolean unsafe, ExtractType type, ExtractFilter filter,
		IAccepted accepted) implements IExtract {}