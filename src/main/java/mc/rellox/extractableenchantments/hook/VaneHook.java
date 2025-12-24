package mc.rellox.extractableenchantments.hook;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.api.item.enchantment.IMetaFetcher;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public class VaneHook implements IHook, IEnchantmentReader {

	@Override
	public String name() {
		return "vane-core";
	}

	@Override
	public void enable() {}

	@Override
	public String key() {
		return name().toLowerCase();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Map<IEnchantment, Integer> enchantments(ItemStack item) {
		Map<IEnchantment, Integer> map = new HashMap<>();
		if(ItemRegistry.nulled(item) || !item.hasItemMeta()) return map;
		
		IMetaFetcher fetcher = EnchantmentRegistry.fetcher(item.getItemMeta());
		fetcher.enchantments().forEach((e, level) -> {
			if(is(e) == true) {
				String key = e.getKey().getKey();
				String name = ChatColor.stripColor(display(e));
				int max = e.getMaxLevel();
				map.put(new VaneEnchantment(e, key, name, max), level);
			}
		});
		return map;
	}
	
	@SuppressWarnings("deprecation")
	private boolean is(Enchantment e) {
		try {
			return e.getKey().getNamespace().equalsIgnoreCase("vane_enchantments");
		} catch (Exception x) {
			RF.debug(x);
		}
		return false;
	}
	
	private String display(Enchantment e) {
		try {
			Object o = RF.direct(e, "getHandle");
			Object custom = RF.direct(o, "custom_enchantment");
			Object name = RF.order(custom, "display_name", int.class).invoke(1);
			return RF.direct(name, "content", String.class);
		} catch (Exception x) {
			RF.debug(x);
		}
		return "";
	}
	
	private static void update(ItemStack item) {
		Class<?> c = RF.get("org.oddlama.vane.core.Core");
		Object core = RF.direct(c, "instance");
		Object manager = RF.fetch(core, "enchantment_manager");
		RF.order(manager, "update_enchanted_item", ItemStack.class).invoke(item);
	}
	
	public record VaneEnchantment(Enchantment enchantment, String key, String name, int maximum) implements IEnchantment {

		@Override
		public void remove(ItemStack item) {
			IEnchantment.removeAsDefault(item, enchantment);
			update(item);
		}

		@Override
		public void apply(ItemStack item, int level) {
			IEnchantment.applyAsDefault(item, enchantment, level);
			update(item);
		}
		
	}

}
