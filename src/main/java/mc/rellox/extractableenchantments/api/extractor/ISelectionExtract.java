package mc.rellox.extractableenchantments.api.extractor;

import java.util.List;

import org.bukkit.inventory.Inventory;

import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;

public interface ISelectionExtract {
	
	/**
	 * @return Selection inventory
	 */
	
	Inventory inventory();
	
	/**
	 * @return Used extractor
	 */
	
	IExtractor extractor();
	
	/**
	 * After any changes to this list you should call {@link #update()}.
	 * 
	 * @return Modifiable list of extractable enchantments
	 */
	
	List<ILevelledEnchantment> enchantments();
	
	/**
	 * Updates the inventory.
	 */
	
	void update();

}
