package mc.rellox.extractableenchantments.hook;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.api.item.enchantment.IMetaFetcher;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

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
			Class<?> rc = RF.get("su.nightexpress.excellentenchants.enchantment.registry.EnchantRegistry");
			@SuppressWarnings("unchecked")
			Map<NamespacedKey, ?> by_key = RF.fetch(rc, "BY_KEY", Map.class);
			by_key.forEach((key, data) -> {
				Enchantment e = RF.direct(data, "getEnchantment", Enchantment.class);
				String name = ChatColor.stripColor(RF.direct(data, "getName", String.class));
				int max = e.getMaxLevel();
				int level = fetcher.level(e);
				if(level <= 0) return;
				map.put(new ExcellentEnchantment(e, key.getKey(), name, max, false), level);
			});
		} catch (Exception x) {
			x.printStackTrace();
		}
		return map;
	}
	
	public record ExcellentEnchantment(Enchantment enchantment, String key, String name, int maximum, boolean curse) implements IEnchantment {

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
