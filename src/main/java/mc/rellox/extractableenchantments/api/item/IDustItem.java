package mc.rellox.extractableenchantments.api.item;

import java.util.stream.Stream;

import org.bukkit.inventory.ItemStack;

public interface IDustItem extends IItem {
	
	/**
	 * Used for the dust recipe result.
	 * 
	 * @return Constant item of the dust
	 */
	
	ItemStack constant();
	
	/**
	 * Used for player usage.
	 * 
	 * @param percent - dust percentage
	 * @return Item of the dust with given percentage
	 */
	
	ItemStack item(int percent);
	
	/**
	 * @param percent - dust percentage
	 * @param amount - amount of items
	 * @return Array of items of the dust with given percentage
	 */
	
	default ItemStack[] items(int percent, int amount) {
		return Stream.generate(() -> item(percent))
				.limit(amount)
				.toArray(ItemStack[]::new);
	}
	
	/**
	 * Update the dust item name and lore.
	 * 
	 * @param item - item
	 */
	
	void update(ItemStack item);

}
