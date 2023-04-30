package mc.rellox.extractableenchantments.supplier;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import com.willfp.ecoenchants.enchants.EcoEnchantLike;

import mc.rellox.extractableenchantments.supplier.EcoEnchantsSupplier.EcoEnchantsVersion;

public class EcoEnchantsVersionNew implements EcoEnchantsVersion {
	
	@Override
	public boolean isEnchantment(Enchantment e) {
		return e instanceof EcoEnchantLike;
	}

	@Override
	public String name(Enchantment e) {
		if(e instanceof EcoEnchantLike ee) return ChatColor.stripColor(ee.getUnformattedDisplayName());
		return "Unknown";
	}

}
