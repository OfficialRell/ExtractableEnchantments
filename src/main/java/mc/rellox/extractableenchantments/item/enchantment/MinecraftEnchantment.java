package mc.rellox.extractableenchantments.item.enchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.configuration.Language;

public record MinecraftEnchantment(Enchantment enchantment, String key, String name) implements IEnchantment {
	
	@SuppressWarnings("deprecation")
	public MinecraftEnchantment(String key, String name) {
		this(Enchantment.getByKey(NamespacedKey.minecraft(key)), key, name);
	}
	
	@Override
	public String name() {
		String text = Language.get("Enchantments." + key).text();
		if(text.isEmpty() == true) return name;
		return text;
	}
	
	@Override
	public boolean minecraft() {
		return true;
	}
	
	@Override
	public boolean curse() {
		return enchantment == Enchantment.BINDING_CURSE
				|| enchantment == Enchantment.VANISHING_CURSE;
	}
	
	@Override
	public void remove(ItemStack item) {
		IEnchantment.removeAsDefault(item, enchantment);
	}

	@Override
	public void apply(ItemStack item, int level) {
		IEnchantment.applyAsDefault(item, enchantment, level);
	}

	@Override
	public int maximum() {
		return enchantment.getMaxLevel();
	}

}
