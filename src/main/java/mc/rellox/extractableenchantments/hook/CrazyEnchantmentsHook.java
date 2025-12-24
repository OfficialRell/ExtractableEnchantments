package mc.rellox.extractableenchantments.hook;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.item.ItemRegistry;

public class CrazyEnchantmentsHook implements IHook, IEnchantmentReader {
	
	private CrazyManager manager;
	private EnchantmentBookSettings settings;

	@Override
	public String name() {
		return "CrazyEnchantments";
	}

	@Override
	public void enable() {
		if(plugin() instanceof CrazyEnchantments ce) {
			manager = ce.getStarter().getCrazyManager();
			settings = ce.getStarter().getEnchantmentBookSettings();
		}
	}

	@Override
	public String key() {
		return name().toLowerCase();
	}

	@Override
	public Map<IEnchantment, Integer> enchantments(ItemStack item) {
		Map<IEnchantment, Integer> map = new HashMap<>();
		if(ItemRegistry.nulled(item) || !item.hasItemMeta()) return map;
		
		Map<CEnchantment, Integer> enchantments = settings.getEnchantments(item);
		if(enchantments == null || enchantments.isEmpty()) return map;
		
		enchantments.forEach((ce, level) -> {
			String name = ce.getCustomName();
			int maximum = ce.getMaxLevel();
			map.put(new CrazyEnchantmentsEnchantment(this, ce, name, maximum), level);
		});
		
		return map;
	}
	
	public record CrazyEnchantmentsEnchantment(CrazyEnchantmentsHook hook, CEnchantment enchantment,
			String name, int maximum) implements IEnchantment {

		@Override
		public void remove(ItemStack item) {
			hook.settings.removeEnchantment(item, enchantment);
		}

		@Override
		public void apply(ItemStack item, int level) {
			hook.manager.addEnchantment(item, enchantment, level);
		}

		@Override
		public String key() {
			return enchantment.getName();
		}
		
	}

}
