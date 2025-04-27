package mc.rellox.extractableenchantments.hook;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;

public final class HookRegistry {
	
	private static final Map<String, IHook> HOOKS = new HashMap<>();
	
	public static final EconomyHook economy = new EconomyHook();
	public static final EcoEnchantsHook eco_enchants = new EcoEnchantsHook();
	public static final ExcellentEnchantsHook excellent_enchants = new ExcellentEnchantsHook();
	public static final VaneHook vane = new VaneHook();
	public static final EnchantmentPlusHook enchantments_plus = new EnchantmentPlusHook();
	public static final AdvancedEnchantmentsHook advanced_enchantments = new AdvancedEnchantmentsHook();
	public static final EnchantsSquaredHook enchants_squared = new EnchantsSquaredHook();
	public static final CrazyEnchantmentsHook crazy_enchantments = new CrazyEnchantmentsHook();
	
	static {
		HOOKS.put(economy.name(), economy);
		HOOKS.put(eco_enchants.name(), eco_enchants);
		HOOKS.put(excellent_enchants.name(), excellent_enchants);
		HOOKS.put(vane.name(), vane);
		HOOKS.put(enchantments_plus.name(), enchantments_plus);
		HOOKS.put(advanced_enchantments.name(), advanced_enchantments);
		HOOKS.put(enchants_squared.name(), enchants_squared);
		HOOKS.put(crazy_enchantments.name(), crazy_enchantments);
	}
	
	public static void initialize() {
		HOOKS.values().stream()
		.filter(IHook::load)
		.peek(IHook::enable)
		.forEach(hook -> {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
					+ ChatColor.DARK_BLUE + hook.name() + " has been found!");
			if(hook instanceof IEnchantmentReader reader)
				EnchantmentRegistry.submit(reader);
		});
	}

}
