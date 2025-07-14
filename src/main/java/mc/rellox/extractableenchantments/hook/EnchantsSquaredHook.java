package mc.rellox.extractableenchantments.hook;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public class EnchantsSquaredHook implements IHook, IEnchantmentReader {
	
	private Object manager;

	@Override
	public String name() {
		return "EnchantsSquared";
	}

	@Override
	public void enable() {
		Class<?> c = RF.get("me.athlaeos.enchantssquared.managers.CustomEnchantManager");
		manager = RF.direct(c, "getInstance");
	}

	@Override
	public String key() {
		return name().toLowerCase();
	}

	@Override
	public Map<IEnchantment, Integer> enchantments(ItemStack item) {
		Map<IEnchantment, Integer> map = new HashMap<>();
		if(ItemRegistry.nulled(item) == true || item.hasItemMeta() == false) return map;
		
		@SuppressWarnings("unchecked")
		Map<Object, Integer> enchantments = RF.order(manager, "getItemsEnchantsFromPDC", ItemStack.class)
				.as(Map.class)
				.invoke(item);
		if(enchantments == null || enchantments.isEmpty() == true) return map;
		
		enchantments.forEach((e, level) -> {
			String type = RF.direct(e, "getType", String.class);
			String name = RF.direct(e, "getDisplayEnchantment", String.class);
			int maximum = RF.order(e, "getMaxLevel")
					.as(int.class)
					.invoke(1);
			map.put(new EnchantsSquaredEnchantment(this, type, name, maximum), level);
		});
		
		return map;
	}
	
	public record EnchantsSquaredEnchantment(EnchantsSquaredHook hook, String enchantment,
			String name, int maximum) implements IEnchantment {

		@Override
		public void remove(ItemStack item) {
			RF.order(hook.manager, "removeEnchant", ItemStack.class, String.class)
				.invoke(item, enchantment);
		}

		@Override
		public void apply(ItemStack item, int level) {
			RF.order(hook.manager, "addEnchant", ItemStack.class, String.class, int.class)
				.invoke(item, enchantment, level);
		}

		@Override
		public String key() {
			return enchantment;
		}
		
	}

}
