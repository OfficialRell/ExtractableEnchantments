package mc.rellox.extractableenchantments.hook;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.item.ItemRegistry;
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
		if(ItemRegistry.nulled(item) == true) return map;
		
		Class<?> c = RF.get("su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant");
		item.getEnchantments().forEach((e, level) -> {
			if(c.isInstance(e) == true) {
				String key = e.getKey().getKey();
				@SuppressWarnings("deprecation")
				String name = ChatColor.stripColor(e.getName());
				int max = e.getMaxLevel();
				map.put(new ExcellentEnchantment(e, key, name, max), level);
			}
		});
		return map;
	}
	
	public record ExcellentEnchantment(Enchantment enchantment, String key, String name, int maximum) implements IEnchantment {

		@Override
		public void remove(ItemStack item) {
			Class<?> c = RF.get("su.nightexpress.excellentenchants.enchantment.util.EnchantUtils");
			RF.order(c, "remove", ItemStack.class, Enchantment.class).invoke(item, enchantment);
			RF.order(c, "updateDisplay", ItemStack.class).invoke(item);
		}

		@Override
		public void apply(ItemStack item, int level) {
			Class<?> c = RF.get("su.nightexpress.excellentenchants.enchantment.util.EnchantUtils");
			RF.order(c, "add", ItemStack.class, Enchantment.class,
					int.class, boolean.class).invoke(item, enchantment, level, true);
			RF.order(c, "updateDisplay", ItemStack.class).invoke(item);
		}
		
	}

}
