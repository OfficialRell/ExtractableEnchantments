package mc.rellox.extractableenchantments.item;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GenericItem extends Item {

	public GenericItem(Material material, boolean glint, int model,
			String tooltip) {
		super(material, List.of(), List.of(), glint, model, tooltip);
	}

	@Override
	public boolean match(ItemStack item) {
		return false;
	}

}
