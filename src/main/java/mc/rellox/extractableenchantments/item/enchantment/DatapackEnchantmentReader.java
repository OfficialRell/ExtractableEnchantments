package mc.rellox.extractableenchantments.item.enchantment;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.api.item.enchantment.IMetaFetcher;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public record DatapackEnchantmentReader(String namespace) implements IEnchantmentReader {

	@Override
	public String key() {
		return namespace;
	}

	@Override
	public Map<IEnchantment, Integer> enchantments(ItemStack item) {
		Map<IEnchantment, Integer> map = new HashMap<>();
		if(ItemRegistry.nulled(item) == true || item.hasItemMeta() == false) return map;

		IMetaFetcher fetcher = EnchantmentRegistry.fetcher(item.getItemMeta());

		fetcher.enchantments().forEach((e, level) -> {
			@SuppressWarnings("deprecation")
			NamespacedKey ns = e.getKey();
			if(ns == null) return;

			String key = ns.getKey();
			if(ns.getNamespace().equals(namespace) == false) return;

			TextComponent t = new TextComponent();
			t.addExtra(new TranslatableComponent(e.getTranslationKey()));
			String name = t.toPlainText();

			int max = e.getMaxLevel();
			map.put(new DatapackEnchantment(e, key, name, max), level);
		});
		return map;
	}

	public record DatapackEnchantment(Enchantment enchantment, String key, String name, int maximum) implements IEnchantment {

		@Override
		public void remove(ItemStack item) {
			IEnchantment.removeAsDefault(item, enchantment);
		}

		@Override
		public void apply(ItemStack item, int level) {
			IEnchantment.applyAsDefault(item, enchantment, level);
		}

	}

}
