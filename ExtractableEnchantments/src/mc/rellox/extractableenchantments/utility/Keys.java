package mc.rellox.extractableenchantments.utility;

import org.bukkit.NamespacedKey;

import mc.rellox.extractableenchantments.ExtractableEnchantments;

public final class Keys {
	
	private static NamespacedKey key_extractor;
	private static NamespacedKey key_dust;
	private static NamespacedKey key_chance;
	private static NamespacedKey key_percent;
	private static NamespacedKey key_random;
	
	public static void initialize() {
		key_extractor = new NamespacedKey(ExtractableEnchantments.instance(), "extractor");
		key_dust = new NamespacedKey(ExtractableEnchantments.instance(), "dust");
		key_chance = new NamespacedKey(ExtractableEnchantments.instance(), "chance");
		key_percent = new NamespacedKey(ExtractableEnchantments.instance(), "chance");
		key_random = new NamespacedKey(ExtractableEnchantments.instance(), "random");
	}
	
	public static NamespacedKey extractor() {
		return key_extractor;
	}
	
	public static NamespacedKey dust() {
		return key_dust;
	}
	
	public static NamespacedKey chance() {
		return key_chance;
	}
	
	public static NamespacedKey percent() {
		return key_percent;
	}
	
	public static NamespacedKey random() {
		return key_random;
	}

}
