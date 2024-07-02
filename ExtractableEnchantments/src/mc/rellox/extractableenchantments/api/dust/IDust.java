package mc.rellox.extractableenchantments.api.dust;

import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.item.IDustItem;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipe;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipeObject;

public interface IDust extends IRecipeObject {
	
	String key();
	
	/**
	 * @return Dust item
	 */
	
	IDustItem item();
	
	/**
	 * @return Dust applicable options
	 */
	
	IApplicable applicable();
	
	IRecipe recipe();
	
	/**
	 * @return Dust percentage limit
	 */
	
	int limit();
	
	/**
	 * @return Crafted item percent
	 */
	
	int percent();
	
	@Override
	default ItemStack result() {
		return item().constant();
	}
	
	@Override
	default String prefix() {
		return "dust_";
	}

}
