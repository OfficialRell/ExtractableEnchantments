package mc.rellox.extractableenchantments.supplier;

import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.extractor.Extractor;

public interface ESupplier<P, E> {
	
	P get();
	
	void load();
	
	boolean isEnchantment(Enchantment e);
	
	String name(E e);
	
	void extract(Extractor ex, Player player, ItemStack item, Enchantment removed, int level,
			boolean failed, boolean storage);
	
	default Set<Object> enchantments(ItemStack item) {
		return null;
	}

}
