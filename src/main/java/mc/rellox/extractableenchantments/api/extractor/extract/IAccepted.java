package mc.rellox.extractableenchantments.api.extractor.extract;

import java.util.Set;

import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;

public interface IAccepted {
	
	/**
	 * @return List of ignored enchantments
	 */
	
	Set<IIgnoredEnchantment> ignored();
	
	/**
	 * When {@code true} - whitelist;<br>
	 * When {@code false} - blacklist.
	 * 
	 * @return Whether the ignored list is inverted
	 */
	
	boolean invert();
	
	/**
	 * @param enchantment - enchantment
	 * @return Whether the enchantment is accepted
	 */
	
	default boolean accepted(ILevelledEnchantment enchantment) {
		IIgnoredEnchantment ignored = ignored().stream()
				.filter(i -> i.enchantment().equals(enchantment.key()))
				.findFirst()
				.orElse(null);
		if(ignored == null) return !invert();
		return ignored.ignore(enchantment.level()) == invert();
	}

}
