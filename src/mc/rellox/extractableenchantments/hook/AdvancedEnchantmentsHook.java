package mc.rellox.extractableenchantments.hook;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public class AdvancedEnchantmentsHook implements IHook, IEnchantmentReader {
	
	private Class<?> AEAPI;

	@Override
	public String name() {
		return "AdvancedEnchantments";
	}

	@Override
	public void enable() {
		AEAPI = RF.get("net.advancedplugins.ae.api.AEAPI");
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
		Map<String, Integer> enchantments = RF.order(AEAPI, "getEnchantmentsOnItem", ItemStack.class)
				.as(Map.class)
				.invoke(item);
		if(enchantments == null || enchantments.isEmpty() == true) return map;
		
		enchantments.forEach((e, level) -> {
			Object ae = RF.order(AEAPI, "getEnchantmentInstance", String.class)
					.invoke(e);
			if(ae == null) return;
			
			String name = RF.direct(ae, "getDisplay", String.class);
			int maximum = RF.order(ae, "getHighestLevel")
					.as(int.class)
					.invoke(1);
			map.put(new AdvancedEnchantment(this, e, name, maximum), level);
		});
		
		return map;
	}
	
	public record AdvancedEnchantment(AdvancedEnchantmentsHook hook, String enchantment,
			String name, int maximum) implements IEnchantment {

		@Override
		public void remove(ItemStack item) {
			RF.order(hook.AEAPI, "removeEnchantment", ItemStack.class, String.class)
				.invoke(item, enchantment);
		}

		@Override
		public void apply(ItemStack item, int level) {
			RF.order(hook.AEAPI, "applyEnchant", String.class, int.class, ItemStack.class)
				.invoke(enchantment, level, item);
		}

		@Override
		public String key() {
			return enchantment;
		}
		
	}

}
