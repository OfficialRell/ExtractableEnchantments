package mc.rellox.extractableenchantments.api.item;

import java.util.stream.Stream;

import org.bukkit.inventory.ItemStack;

public interface IDustItem extends IItem {
	
	ItemStack constant();
	
	ItemStack item(int percent);
	
	default ItemStack[] items(int percent, int a) {
		return Stream.generate(() -> item(percent))
				.limit(a)
				.toArray(ItemStack[]::new);
	}
	
	void update(ItemStack item);

}
