package mc.rellox.extractableenchantments.api.item.enchantment;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public interface IMetaFetcher {
	
	/**
	 * @param enchantment - enchantment
	 * @return Enchantment level
	 */
	
	int level(Enchantment enchantment);
	
	/**
	 * @return All enchantments and their levels
	 */
	
	Map<Enchantment, Integer> enchantments();

}
