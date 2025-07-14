package mc.rellox.extractableenchantments.api.item.enchantment;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;

/**
 * Implement this interface to your enchantment reader class
 *  and then submit it to the enchantment registry:<br>
 *  <pre>{@link EnchantmentRegistry#submit(IEnchantmentReader)}</pre>
 * 
 */

public interface IEnchantmentReader {
	
	/**
	 * @return Enchantment reader key (mostly plugin name in lowercase)
	 */
	
	String key();
	
	/**
	 * This method should only return the map of custom enchantments
	 *  that only this reader returns.
	 * 
	 * @param item - item
	 * @return Map of enchantments and their levels from the item
	 */
	
	Map<IEnchantment, Integer> enchantments(ItemStack item);

}
