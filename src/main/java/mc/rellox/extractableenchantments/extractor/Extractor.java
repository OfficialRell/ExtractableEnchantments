package mc.rellox.extractableenchantments.extractor;

import java.util.List;

import mc.rellox.extractableenchantments.api.extractor.IChanceOverride;
import mc.rellox.extractableenchantments.api.extractor.IExtractPrice;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.extractor.IExtractorChance;
import mc.rellox.extractableenchantments.api.extractor.constraint.IConstraint;
import mc.rellox.extractableenchantments.api.extractor.extract.IExtract;
import mc.rellox.extractableenchantments.api.item.IExtractorItem;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipe;

public record Extractor(String key, IExtractorItem item, IExtractorChance chance,
		boolean clearing, IChanceOverride override, IExtract extract,
		IExtractPrice price, List<IConstraint> constraints,
		IRecipe recipe, boolean stackable) implements IExtractor {}
