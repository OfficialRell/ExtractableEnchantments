package mc.rellox.extractableenchantments.item.enchantment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.extractor.extract.ExtractFilter;
import mc.rellox.extractableenchantments.api.extractor.extract.IExtract;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IMetaFetcher;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.item.ItemRegistry;

public final class EnchantmentRegistry {

	private static final Map<String, IEnchantmentReader> READERS = new HashMap<>();
	
	public static final Map<String, IEnchantment> ENCHANTMENTS = new HashMap<>();
	static {
		put("aqua_affinity", "Aqua Affinity");
		put("bane_of_arthropods", "Bane of Arthropods");
		put("binding_curse", "Curse of Binding");
		put("blast_protection", "Blast Protection");
		put("breach", "Breach");
		put("channeling", "Channeling");
		put("density", "Density");
		put("depth_strider", "Depth Strider");
		put("efficiency", "Efficiency");
		put("feather_falling", "Feather Falling");
		put("fire_aspect", "Fire Aspect");
		put("fire_protection", "Fire Protection");
		put("flame", "Flame");
		put("fortune", "Fortune");
		put("frost_walker", "Frost Walker");
		put("impaling", "Impaling");
		put("infinity", "Infinity");
		put("knockback", "Knockback");
		put("luck_of_the_sea", "Luck of the Sea");
		put("loyalty", "Loyalty");
		put("looting", "Looting");
		put("lunge", "Lunge");
		put("lure", "Lure");
		put("mending", "Mending");
		put("multishot", "Multishot");
		put("piercing", "Piercing");
		put("power", "Power");
		put("projectile_protection", "Projectile Protection");
		put("protection", "Protection");
		put("punch", "Punch");
		put("quick_charge", "Quick Charge");
		put("respiration", "Respiration");
		put("riptide", "Riptide");
		put("sharpness", "Sharpness");
		put("silk_touch", "Silk Touch");
		put("smite", "Smite");
		put("soul_speed", "Soul Speed");
		put("sweeping_edge", "Sweeping Edge");
		put("swift_sneak", "Swift Sneak");
		put("thorns", "Thorns");
		put("unbreaking", "Unbreaking");
		put("vanishing_curse", "Curse of Vanishing");
		put("wind_burst", "Wind Burst");
	}
	
	private static void put(String key, String name) {
		MinecraftEnchantment enchantment = new MinecraftEnchantment(key, name);
		if(enchantment.enchantment() == null) return;
		ENCHANTMENTS.put(key, enchantment);
	}
	
	public static void initialize() {
		submit(new IEnchantmentReader() {
			@Override
			public String key() {
				return "minecraft";
			}
			@Override
			public Map<IEnchantment, Integer> enchantments(ItemStack item) {
				Map<IEnchantment, Integer> map = new HashMap<>();
				if(ItemRegistry.nulled(item) || !item.hasItemMeta()) return map;
				
				IMetaFetcher fetcher = EnchantmentRegistry.fetcher(item.getItemMeta());
				fetcher.enchantments().forEach((e, l) -> {
					@SuppressWarnings("deprecation")
					NamespacedKey key = e.getKey();
					if(key == null) return;
					IEnchantment enchantment = ENCHANTMENTS.get(key.getKey());
					if(enchantment == null) return;
					map.put(enchantment, l);
				});
				return map;
			}
		});
	}

	public static void submit(IEnchantmentReader reader) {
		String key = reader.key();
		if(READERS.containsKey(key))
			throw new IllegalArgumentException("Duplicate enchantment reader with key: " + key);
		READERS.put(key, reader);
	}

	public static void remove(Predicate<IEnchantmentReader> filter) {
		var it = READERS.values().iterator();
		if(it.hasNext())
			if(filter.test(it.next()))
				it.remove();
	}
	
	public static List<ILevelledEnchantment> enchantments(ItemStack item) {
		return READERS.values().stream()
				.map(reader -> reader.enchantments(item))
				.flatMap(map -> map.entrySet().stream()
						.map(entry -> new LevelledEnchantment(entry.getKey(), entry.getValue())))
				.collect(Collectors.toList());
	}
	
	public static Map<String, ILevelledEnchantment> keyed(ItemStack item) {
		return enchantments(item).stream()
				.collect(Collectors.toMap(i -> i.enchantment().key(), Function.identity()));
	}

	public static List<ILevelledEnchantment> enchantments(IExtractor extractor, Player player, ItemStack item) {
		List<ILevelledEnchantment> enchantments = EnchantmentRegistry.enchantments(item);
		
		if(enchantments.isEmpty()) return enchantments;
		
		IExtract extract = extractor.extract();
		enchantments.removeIf(e -> !extract.filter().accepts(e.enchantment()));
		
		if(enchantments.isEmpty()) {
			if(extract.filter() == ExtractFilter.MINECRAFT)
				Language.get("Extraction.filter.minecraft").send(player);
			else if(extract.filter() == ExtractFilter.CUSTOM)
				Language.get("Extraction.filter.custom").send(player);
			return enchantments;
		}
		if(!extract.unsafe()) {
			enchantments.removeIf(ILevelledEnchantment::unsafe);
			if(enchantments.isEmpty()) {
				Language.get("Extraction.unsafe").send(player);
				return enchantments;
			}
		}
		enchantments.removeIf(e -> !extract.accepted().accepted(e));
		
		return enchantments;
	}
	
	public static IMetaFetcher fetcher(ItemMeta meta) {
		if(meta instanceof EnchantmentStorageMeta storage) {
			return new IMetaFetcher() {
				@Override
				public int level(Enchantment enchantment) {
					return storage.getStoredEnchantLevel(enchantment);
				}
				@Override
				public Map<Enchantment, Integer> enchantments() {
					return storage.getStoredEnchants();
				}
			};
		}
		return new IMetaFetcher() {
			@Override
			public int level(Enchantment enchantment) {
				return meta.getEnchantLevel(enchantment);
			}
			@Override
			public Map<Enchantment, Integer> enchantments() {
				return meta.getEnchants();
			}
		};
	}
	
}
