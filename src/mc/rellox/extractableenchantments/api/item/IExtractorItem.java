package mc.rellox.extractableenchantments.api.item;

import java.util.stream.Stream;

import org.bukkit.inventory.ItemStack;

public interface IExtractorItem extends IItem {
	
	ItemStack constant();
	
	ItemStack item();
	
	default ItemStack[] items(int a) {
		return Stream.generate(this::item)
				.limit(a)
				.toArray(ItemStack[]::new);
	}

}
