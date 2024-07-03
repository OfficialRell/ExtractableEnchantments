package mc.rellox.extractableenchantments.api.extractor.extract;

import java.util.Set;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;

public interface IAccepted {
	
	Set<String> enchantments();
	
	boolean invert();
	
	default boolean accepted(IEnchantment enchantment) {
		return enchantments().contains(enchantment.key()) == invert();
	}

}
