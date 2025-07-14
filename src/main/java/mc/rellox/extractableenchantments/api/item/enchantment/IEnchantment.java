package mc.rellox.extractableenchantments.api.item.enchantment;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * When adding support for this plugin you should
 *  create a class that implements this interface.
 */

public interface IEnchantment {
	
	/**
	 * @return Enchantment key
	 */
	
	String key();
	
	/**
	 * @return Enchantment name without any color or formatting
	 */
	
	String name();
	
	/**
	 * @return Maximum enchantment level
	 */
	
	int maximum();
	
	/**
	 * Removes this enchantment from the item.
	 * 
	 * @param item - item
	 */
	
	void remove(ItemStack item);
	
	/**
	 * Applies this enchantment to this item.
	 * 
	 * @param item - item
	 * @param level - level
	 */
	
	void apply(ItemStack item, int level);

	/**
	 * @param level - level
	 * @return Book with this enchantment
	 */
	
	default ItemStack book(int level) {
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		apply(item, level);
		return item;
	}
	
	/**
	 * This should always be false if you are implementing custom enchantments.
	 * 
	 * @return If this enchantment is in vanilla minecraft
	 */
	
	default boolean minecraft() {
		return false;
	}
	
	/**
	 * @return If this enchantment is a curse
	 */
	
	default boolean curse() {
		return false;
	}
	
	static void removeAsDefault(ItemStack item, Enchantment enchantment) {
		ItemMeta meta = item.getItemMeta();
		if(meta instanceof EnchantmentStorageMeta storage)
				storage.removeStoredEnchant(enchantment);
		else meta.removeEnchant(enchantment);
		item.setItemMeta(meta);
	}
	
	static void applyAsDefault(ItemStack item, Enchantment enchantment, int level) {
		ItemMeta meta = item.getItemMeta();
		if(meta instanceof EnchantmentStorageMeta storage)
				storage.addStoredEnchant(enchantment, level, true);
		else meta.addEnchant(enchantment, level, true);
		item.setItemMeta(meta);}

}
