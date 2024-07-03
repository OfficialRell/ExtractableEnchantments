package mc.rellox.extractableenchantments.api.item.enchantment;

import org.bukkit.inventory.ItemStack;

public interface ILevelledEnchantment {
	
	IEnchantment enchantment();
	
	int level();
	
	default boolean unsafe() {
		return level() > enchantment().maximum();
	}
	
	default ItemStack book() {
		return enchantment().book(level());
	}
	
	default void remove(ItemStack item) {
		enchantment().remove(item);
	}
	
	default void apply(ItemStack item) {
		enchantment().apply(item, level());
	}

}
