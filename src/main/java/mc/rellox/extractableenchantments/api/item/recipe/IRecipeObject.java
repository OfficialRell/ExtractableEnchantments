package mc.rellox.extractableenchantments.api.item.recipe;

import org.bukkit.inventory.ItemStack;

public interface IRecipeObject {
	
	/**
	 * @return Key
	 */
	
	String key();
	
	/**
	 * @return Recipe key prefix
	 */
	
	String prefix();
	
	/**
	 * @return Constant result item
	 */
	
	ItemStack result();
	
	/**
	 * Returns the recipe.<br>
	 * Will return {@link IRecipe#empty} if disabled.
	 * 
	 * @return The recipe
	 */
	
	IRecipe recipe();

}
