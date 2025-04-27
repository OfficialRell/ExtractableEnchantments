package mc.rellox.extractableenchantments.api.item;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.text.content.Content;
import mc.rellox.extractableenchantments.utility.Utility;

public interface IItem {
	
	Material material();
	
	List<Content> name();
	
	List<Content> info();
	
	boolean glint();
	
	String tooltip();
	
	int model();
	
	boolean match(ItemStack item);
	
	@SuppressWarnings("deprecation")
	default ItemStack generic() {
		ItemStack item = new ItemStack(material());
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(Stream.of(ItemFlag.values())
				.filter(i -> i.ordinal() < 8)
				.toArray(ItemFlag[]::new));
		
		if(glint() == true) ItemRegistry.glint(meta);
		if(model() > 0) meta.setCustomModelData(model());
		
		Utility.tooltip(meta, tooltip());

		item.setItemMeta(meta);
		return item;
	}
	
}
