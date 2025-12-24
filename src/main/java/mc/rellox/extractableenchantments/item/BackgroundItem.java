package mc.rellox.extractableenchantments.item;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.utility.Version;
import mc.rellox.extractableenchantments.utility.Version.VersionType;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public class BackgroundItem extends Item {
	
	public static final BackgroundItem nulled = new BackgroundItem(Material.AIR, 0, "") {
		@Override
		public ItemStack generic() {
			return null;
		}
	};

	public BackgroundItem(Material material, int model, String tooltip) {
		super(material, List.of(), List.of(), false, model, tooltip);
	}

	@Override
	public boolean match(ItemStack item) {
		return false;
	}
	
	@Override
	public ItemStack generic() {
		ItemStack item = super.generic();
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		if(Version.version.atleast(VersionType.v_20_4))
			RF.order(meta, "setHideTooltip", boolean.class).invoke(true);
		item.setItemMeta(meta);
		
		return item;
	}

}
