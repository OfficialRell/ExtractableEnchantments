package mc.rellox.extractableenchantments.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.extractor.extract.ExtractType;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.configuration.Settings;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.text.Text;
import mc.rellox.extractableenchantments.text.content.Content;
import mc.rellox.extractableenchantments.utility.Keys;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public final class ExtractorRegistry {

	private static final Map<String, IExtractor> EXTACTORS = new HashMap<>();
	
	public static void add(IExtractor extractor) {
		String key = extractor.key();
		if(EXTACTORS.containsKey(key) == true)
			throw new IllegalArgumentException("Diplicate extractor with key: " + key);
		EXTACTORS.put(key, extractor);
	}
	
	public static void clear() {
		EXTACTORS.clear();
	}
	
	public static List<IExtractor> all() {
		return new ArrayList<>(EXTACTORS.values());
	}
	
	public static IExtractor get(String key) {
		return EXTACTORS.get(key);
	}
	
	public static IExtractor get(ItemStack item) {
		return EXTACTORS.values()
				.stream()
				.filter(e -> e.item().match(item))
				.findFirst()
				.orElse(null);
	}
	
	public static void extract(IExtractor extractor, Player player, ItemStack item,
			ILevelledEnchantment to_remove, boolean failed) {
		IEnchantment enchantment = to_remove.enchantment();
		
		if(failed == true) {
			if(extractor.extract().type() == ExtractType.RANDOM) player.setItemOnCursor(null);
			if(extractor.chance().destroy() == true) {
				enchantment.remove(item);
				Language.get("Extraction.destroy",
						"enchantment", enchantment.name(),
						"level", Text.roman(to_remove.level())).send(player);
				Settings.settings.sound_extract_destroy.play(player);
				Settings.settings.sound_extract_fail.play(player);
			} else {
				Language.get("Extraction.fail",
						"enchantment", enchantment.name(),
						"level", Text.roman(to_remove.level())).send(player);
				Settings.settings.sound_extract_fail.play(player);
			}
			return;
		}

		enchantment.remove(item);
		
		if(extractor.clearing() == false) {
			ItemStack book = to_remove.book();
			if(Settings.settings.book_chance_enabled == true) {
				int chance = extractor.override().enabled()
						? extractor.override().value() : Settings.book();
				ItemMeta meta = book.getItemMeta();
				List<String> lore = new ArrayList<>();
				lore.add("");
				List<Content> list = Language.list("Book.info.chance", "chance", chance);
				list.replaceAll(c -> Content.of(Content.of(Language.prefix_chance), c));
				lore.addAll(Text.toText(list));
				meta.setLore(lore);
				PersistentDataContainer data = meta.getPersistentDataContainer();
				data.set(Keys.chance(), PersistentDataType.INTEGER, chance);
				book.setItemMeta(meta);
			}
			
			if(extractor.extract().type() == ExtractType.RANDOM) {
				if(player.getGameMode() == GameMode.CREATIVE) player.getInventory().addItem(book);
				else player.setItemOnCursor(book);
			} else {
				if(ItemRegistry.free(player) <= 0) player.getWorld().dropItem(player.getLocation(), book);
				else player.getInventory().addItem(book);
			}
		} else if(extractor.extract().type() == ExtractType.RANDOM) player.setItemOnCursor(null);
		
		Language.get("Extraction.success",
				"enchantment", enchantment.name(),
				"level", Text.roman(to_remove.level())).send(player);

		Settings.settings.sound_extract_success.play(player);
		
		DustOptions o = new DustOptions(Color.BLACK, 1f);
		player.spawnParticle(RF.enumerate(Particle.class, "DUST", "REDSTONE"),
				player.getLocation().add(0, 1, 0), 50, 0.2, 0.5, 0.2, 0, o);
	}
	
	public static boolean contains(ItemStack[] matrix) {
		return Stream.of(matrix)
				.filter(Objects::nonNull)
				.map(ExtractorRegistry::get)
				.filter(Objects::nonNull)
				.count() > 0;
	}
}
