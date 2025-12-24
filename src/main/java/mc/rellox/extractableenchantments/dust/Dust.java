package mc.rellox.extractableenchantments.dust;

import mc.rellox.extractableenchantments.api.dust.IApplicable;
import mc.rellox.extractableenchantments.api.dust.IDust;
import mc.rellox.extractableenchantments.api.item.IDustItem;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipe;

public record Dust(String key, IDustItem item, IApplicable applicable,
		IRecipe recipe, int limit, int percent) implements IDust {}
