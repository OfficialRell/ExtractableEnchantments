package mc.rellox.extractableenchantments.api.extractor;

import java.util.List;

import org.bukkit.inventory.Inventory;

import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;

public interface ISelectionExtract {
	
	Inventory inventory();
	
	IExtractor extractor();
	
	List<ILevelledEnchantment> enchantments();
	
	void update();

}
