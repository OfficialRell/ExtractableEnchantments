package mc.rellox.extractableenchantments.api.extractor.extract;

public interface IIgnoredEnchantment {
	
	static IIgnoredEnchantment of(String enchantment) {
		return new IIgnoredEnchantment() {
			@Override
			public boolean ignore(int level) {
				return true;
			}
			@Override
			public String enchantment() {
				return enchantment;
			}
		};
	}
	
	static IIgnoredEnchantment of(String enchantment, int level) {
		return new IIgnoredEnchantment() {
			@Override
			public boolean ignore(int input) {
				return level == input;
			}
			@Override
			public String enchantment() {
				return enchantment;
			}
		};
	}
	
	static IIgnoredEnchantment of(String enchantment, int minimum, int maximum) {
		return new IIgnoredEnchantment() {
			@Override
			public boolean ignore(int level) {
				return level >= minimum && level <= maximum;
			}
			@Override
			public String enchantment() {
				return enchantment;
			}
		};
	}
	
	/**
	 * @return The ignorable enchantment key
	 */
	
	String enchantment();
	
	/**
	 * @param level - level
	 * @return {@code true} if this enchantment cannot be extracted
	 */
	
	boolean ignore(int level);

}
