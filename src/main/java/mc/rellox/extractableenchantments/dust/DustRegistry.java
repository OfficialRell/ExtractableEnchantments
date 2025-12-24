package mc.rellox.extractableenchantments.dust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import mc.rellox.extractableenchantments.api.dust.IDust;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.utility.Keys;

public final class DustRegistry {
	
	public static final Map<String, IDust> DUST = new HashMap<>();
	
	public static void add(IDust dust) {
		String key = dust.key();
		if(DUST.containsKey(key))
			throw new IllegalArgumentException("Diplicate dust with key: " + key);
		DUST.put(key, dust);
	}
	
	public static void clear() {
		DUST.clear();
	}
	
	public static List<IDust> all() {
		return new ArrayList<>(DUST.values());
	}

	public static IDust get(String key) {
		return DUST.get(key);
	}
	
	public static IDust get(ItemStack item) {
		return DUST.values()
				.stream()
				.filter(e -> e.item().match(item))
				.findFirst()
				.orElse(null);
	}
	
	public static int readPercent(ItemStack item) {
		if(ItemRegistry.nulled(item)) return -1;
		PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
		return data.getOrDefault(Keys.percent(), PersistentDataType.INTEGER, -1);
	}
	
	public static void writePercent(ItemStack item, int percent) {
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(Keys.percent(), PersistentDataType.INTEGER, percent);
		item.setItemMeta(meta);
	}
	
	public static int readChance(ItemStack item) {
		if(ItemRegistry.nulled(item)) return -1;
		PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
		return data.getOrDefault(Keys.chance(), PersistentDataType.INTEGER, -1);
	}
	
	public static void writeChance(ItemStack item, int chance) {
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(Keys.chance(), PersistentDataType.INTEGER, chance);
		item.setItemMeta(meta);
	}
	
	public static boolean contains(ItemStack[] matrix) {
		return Stream.of(matrix)
				.filter(Objects::nonNull)
				.map(DustRegistry::get)
				.filter(Objects::nonNull)
				.count() > 0;
	}
}
