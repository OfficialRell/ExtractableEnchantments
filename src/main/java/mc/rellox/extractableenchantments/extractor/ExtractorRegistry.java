package mc.rellox.extractableenchantments.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.GameMode;
import org.bukkit.Particle;
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

	private static final Map<String, IExtractor> EXTRACTORS = new HashMap<>();
	
	public static void add(IExtractor extractor) {
		String key = extractor.key();
		if(EXTRACTORS.containsKey(key))
			throw new IllegalArgumentException("Diplicate extractor with key: " + key);
		EXTRACTORS.put(key, extractor);
	}
	
	public static void clear() {
		EXTRACTORS.clear();
	}
	
	public static List<IExtractor> all() {
		return new ArrayList<>(EXTRACTORS.values());
	}
	
	public static IExtractor get(String key) {
		return EXTRACTORS.get(key);
	}
	
	public static IExtractor get(ItemStack item) {
		return EXTRACTORS.values()
				.stream()
				.filter(e -> e.item().match(item))
				.findFirst()
				.orElse(null);
	}
	
	public static void extract(IExtractor extractor, Player player, ItemStack item_enchanted,
			ItemStack item_extractor, ILevelledEnchantment to_remove) {
		extract(extractor, player, item_enchanted, item_extractor, to_remove, extractor.extract().type());
	}
	
	public static void extract(IExtractor extractor, Player player, ItemStack item_enchanted,
			ItemStack item_extractor, ILevelledEnchantment to_remove, ExtractType extraction) {
		IEnchantment enchantment = to_remove.enchantment();
		
		boolean success = extractor.chance().chance(item_extractor);
		
		if(!success) {
			if(extraction == ExtractType.RANDOM) player.setItemOnCursor(null);
			String l;
			if(extractor.chance().destroy()) {
				enchantment.remove(item_enchanted);
				l = "Extraction.destroy";
				Settings.settings.sound_extract_destroy.play(player);
			} else l = "Extraction.fail";
			Language.get(l, "enchantment", enchantment.name(),
					"level", Text.roman(to_remove.level())).send(player);
			player.spawnParticle(RF.enumerate(Particle.class, "SMOKE_NORMAL", "SMOKE"),
					player.getLocation().add(0, 1, 0), 75, 0.2, 0.4, 0.2, 0.1);
			Settings.settings.sound_extract_fail.play(player);
			return;
		}

		enchantment.remove(item_enchanted);
		
		if(!extractor.clearing()) {
			ItemStack book = to_remove.book();
			if(Settings.settings.book_chance_enabled) {
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
			
			if(extraction == ExtractType.RANDOM) {
				if(player.getGameMode() == GameMode.CREATIVE) player.getInventory().addItem(book);
				else player.setItemOnCursor(book);
			} else {
				if(ItemRegistry.free(player) <= 0) player.getWorld().dropItem(player.getLocation(), book);
				else player.getInventory().addItem(book);
			}
		} else if(extraction == ExtractType.RANDOM) player.setItemOnCursor(null);
		
		Language.get("Extraction.success",
				"enchantment", enchantment.name(),
				"level", Text.roman(to_remove.level())).send(player);

		Settings.settings.sound_extract_success.play(player);
		
		player.spawnParticle(RF.enumerate(Particle.class, "FIREWORKS_SPARK", "FIREWORK"),
				player.getLocation().add(0, 1, 0), 50, 0.2, 0.4, 0.2, 0.1);
	}
	
	public static boolean contains(ItemStack[] matrix) {
		return Stream.of(matrix)
				.filter(Objects::nonNull)
				.map(ExtractorRegistry::get)
				.filter(Objects::nonNull)
				.count() > 0;
	}
}
