package mc.rellox.extractableenchantments.supplier;

import java.lang.reflect.Method;

import org.bukkit.enchantments.Enchantment;

import mc.rellox.extractableenchantments.supplier.EcoEnchantsSupplier.EcoEnchantsVersion;

public class EcoEnchantsVersionOld implements EcoEnchantsVersion {

	@Override
	public boolean isEnchantment(Enchantment e) {
		try {
			Class<?> c = Class.forName("com.willfp.ecoenchants.enchantments.EcoEnchant");
			return c.isInstance(e);
		} catch (ClassNotFoundException x) {}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String name(Enchantment e) {
		try {
			Class<?> a = Class.forName("com.willfp.ecoenchants.display.EnchantmentCache");
			Method m = a.getMethod("getEntry", Enchantment.class);
			Object o = m.invoke(null, e);
			String s = (String) o.getClass().getMethod("getName").invoke(o);
			return s;
		} catch (Exception x) {}
		return e.getName();
	}

}
