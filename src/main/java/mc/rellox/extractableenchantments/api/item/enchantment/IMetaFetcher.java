package mc.rellox.extractableenchantments.api.item.enchantment;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public interface IMetaFetcher {
	
	int level(Enchantment enchantment);
	
	Map<Enchantment, Integer> enchantments();

}
