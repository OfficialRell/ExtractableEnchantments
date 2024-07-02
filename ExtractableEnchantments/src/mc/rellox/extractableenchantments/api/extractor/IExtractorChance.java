package mc.rellox.extractableenchantments.api.extractor;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import mc.rellox.extractableenchantments.utility.Keys;
import mc.rellox.extractableenchantments.utility.Utility;

public interface IExtractorChance {
	
	static IExtractorChance empty = new IExtractorChance() {
		@Override
		public int minimum() {return 100;}
		@Override
		public int maximum() {return 100;}
		@Override
		public boolean enabled() {return false;}
		@Override
		public boolean destroy() {return false;}
	};
	
	boolean enabled();
	
	boolean destroy();
	
	int minimum();
	
	int maximum();
	
	default int roll() {
		return Utility.between(minimum(), maximum());
	}
	
	default boolean chance(ItemStack item) {
		if(enabled() == false) return true;
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer p = meta.getPersistentDataContainer();
		return Utility.random(100) <= p.getOrDefault(Keys.chance(), PersistentDataType.INTEGER, 100);
		
	}

}
