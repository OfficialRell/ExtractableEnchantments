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
import mc.rellox.extractableenchantments.utility.reflect.type.Invoker;

public class ExcellentEnchantsHook implements IHook, IEnchantmentReader {
	
	private Class<?> registry_class;
	private Invoker<Enchantment> get_enchantment;
	private Invoker<String> get_name;

	@Override
	public String name() {
		return "ExcellentEnchants";
	}

	@Override
	public void enable() {
		try {
			registry_class = RF.get("su.nightexpress.excellentenchants.registry.EnchantRegistry");
			if(registry_class == null)
				registry_class = RF.get("su.nightexpress.excellentenchants.enchantments.registry.EnchantRegistry");
			
			get_enchantment = RF.order(registry_class, "getBukkitEnchantment", false).as(Enchantment.class);
			if(get_enchantment.valid() == false)
				get_enchantment = RF.order(registry_class, "getEnchantment", false).as(Enchantment.class);
			
			get_name = RF.order(registry_class, "getDisplayName", false).as(String.class);
			if(get_name.valid() == false)
				get_name = RF.order(registry_class, "getName", false).as(String.class);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
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
			@SuppressWarnings("unchecked")
			Map<NamespacedKey, ?> by_key = RF.fetch(registry_class, "BY_KEY", Map.class);
			by_key.forEach((key, data) -> {
				Enchantment e = get_enchantment.objected(data);
				String name = ChatColor.stripColor(get_name.objected(data));
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
