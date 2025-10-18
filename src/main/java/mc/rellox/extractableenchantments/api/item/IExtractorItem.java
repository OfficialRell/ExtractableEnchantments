package mc.rellox.extractableenchantments.api.item;

import java.util.stream.Stream;

import org.bukkit.inventory.ItemStack;

public interface IExtractorItem extends IItem {

	/**
	 * Used for the extractor recipe result.
	 * 
	 * @return Constant item of the extractor
	 */
	
	ItemStack constant();
	
	/**
	 * Used for player usage.
	 * 
	 * @return Item of the extractor
	 */
	
	ItemStack item();
	
	/**
	 * @param amount - amount of items
	 * @return Array of items of the extractor
	 */
	
	default ItemStack[] items(int amount) {
		return Stream.generate(this::item)
				.limit(amount)
				.toArray(ItemStack[]::new);
	}

}
