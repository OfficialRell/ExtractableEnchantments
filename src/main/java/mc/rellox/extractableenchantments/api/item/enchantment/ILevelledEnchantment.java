package mc.rellox.extractableenchantments.api.item.enchantment;

import org.bukkit.inventory.ItemStack;

public interface ILevelledEnchantment {
	
	/**
	 * @return Enchantment
	 */
	
	IEnchantment enchantment();
	
	/**
	 * @return Enchantment key
	 */
	
	default String key() {
		return enchantment().key();
	}
	
	/**
	 * @return Enchantment level
	 */
	
	int level();
	
	/**
	 * @return Whether the enchantment level exceeds the maximum level
	 */
	
	default boolean unsafe() {
		return level() > enchantment().maximum();
	}
	
	/**
	 * @return Enchanted book with this enchantment
	 */
	
	default ItemStack book() {
		return enchantment().book(level());
	}
	
	/**
	 * Removes this enchantment from the item.
	 * 
	 * @param item - item
	 */
	
	default void remove(ItemStack item) {
		enchantment().remove(item);
	}
	
	/**
	 * Applies this enchantment to the item.
	 * 
	 * @param item - item
	 */
	
	default void apply(ItemStack item) {
		enchantment().apply(item, level());
	}

}
