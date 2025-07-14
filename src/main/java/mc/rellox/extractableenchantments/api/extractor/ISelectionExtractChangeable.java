package mc.rellox.extractableenchantments.api.extractor;

import org.bukkit.inventory.ItemStack;

public interface ISelectionExtractChangeable extends ISelectionExtract {
	
	/**
	 * Sets the item from which enchantments will be extracted and updates the inventory.<br>
	 * Set {@code null} to clear.
	 * 
	 * @param item - new item
	 */
	
	void set(ItemStack item);
	
	/**
	 * @return The item from which enchantments will be extracted or {@code null}
	 */
	
	ItemStack item();

}
