package mc.rellox.extractableenchantments.hook;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.api.item.enchantment.IMetaFetcher;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;
import su.nightexpress.excellentenchants.api.EnchantRegistry;

public class ExcellentEnchantsHook implements IHook, IEnchantmentReader {

	@Override
	public String name() {
		return "ExcellentEnchants";
	}

	@Override
	public void enable() {}
	
	@Override
	public String key() {
		return name().toLowerCase();
	}

	@Override
	public Map<IEnchantment, Integer> enchantments(ItemStack item) {
		Map<IEnchantment, Integer> map = new HashMap<>();
		if(ItemRegistry.nulled(item) == true || item.hasItemMeta() == false) return map;
		
		ItemMeta meta = item.getItemMeta();
		IMetaFetcher fetcher = EnchantmentRegistry.fetcher(meta);
		
		try {
			fetcher.enchantments().forEach((enchantment, level) -> {
				var custom = EnchantRegistry.getByBukkit(enchantment);
				if(custom == null) return;
				var name = ChatColor.stripColor(custom.getDisplayName());
				var key = custom.getId();
				enchantment = custom.getBukkitEnchantment();
				var maximum = enchantment.getMaxLevel();
				var curse = custom.isCurse();
				map.put(new ExcellentEnchantment(enchantment, key, name, maximum, curse), level);
			});
		} catch (Exception x) {
			RF.debug(x);
		}
		return map;
	}
	
	public record ExcellentEnchantment(Enchantment enchantment, String key, String name, int maximum, boolean curse)
	implements IEnchantment {

		@Override
		public void remove(ItemStack item) {
			IEnchantment.removeAsDefault(item, enchantment);
		}

		@Override
		public void apply(ItemStack item, int level) {
			IEnchantment.applyAsDefault(item, enchantment, level);
		}
		
	}

}
