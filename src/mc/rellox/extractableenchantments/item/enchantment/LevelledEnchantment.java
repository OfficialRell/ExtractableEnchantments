package mc.rellox.extractableenchantments.item.enchantment;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;

public record LevelledEnchantment(IEnchantment enchantment, int level) implements ILevelledEnchantment {

	@Override
	public int hashCode() {
		return enchantment.key().hashCode();
	}

}
