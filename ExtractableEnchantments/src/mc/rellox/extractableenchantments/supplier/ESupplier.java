package mc.rellox.extractableenchantments.supplier;

import java.util.Set;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.extractor.Extractor;

public interface ESupplier<P, E> extends HookInstance<P> {
	
	static HookInstance<?> of(HookType type) {
		if(Bukkit.getPluginManager().getPlugin(type.plugin) == null) return null;
		return type.s.get();
	}
	
	boolean isEnchantment(Enchantment e);
	
	String name(E e);
	
	void extract(Extractor ex, Player player, ItemStack item, Enchantment removed, int level,
			boolean failed, boolean storage);
	
	default Set<Object> enchantments(ItemStack item) {
		return null;
	}
	
	public enum HookType {
		
		economy("Vault", EconomySupplier::new),
		excellent_enchant("ExcellentEnchants", ExcellentEnchantsSupplier::new),
		custom_enchants("CustomEnchantments", CustomEnchantsSupplier::new),
		eco_enchants("EcoEnchants", EcoEnchantsSupplier::new);
		
		private final String plugin;
		private final Supplier<HookInstance<?>> s;
		
		private HookType(String plugin, Supplier<HookInstance<?>> s) {
			this.plugin = plugin;
			this.s = s;
		}
	}

}
