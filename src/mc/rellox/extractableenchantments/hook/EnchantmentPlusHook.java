package mc.rellox.extractableenchantments.hook;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantmentReader;
import mc.rellox.extractableenchantments.api.item.enchantment.IMetaFetcher;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class EnchantmentPlusHook implements IHook, IEnchantmentReader {

	@Override
	public String name() {
		return "Enchantments+";
	}
	
	@Override
	public boolean load() {
		try {
			World world = Bukkit.getWorld("world");
			if(world == null) return false;
			File folder = world.getWorldFolder();
			File datapacks = new File(folder, "datapacks");
			if(datapacks.exists() == false || datapacks.isDirectory() == false) return false;
			File[] files = datapacks.listFiles();
			if(files == null || files.length <= 0) return false;
			for(File file : files)
				if(file.getName().contains("enchantment-plus") == true)
					return true;
		} catch (Exception e) {
			RF.debug(e);
		}
		return false;
	}
	
	@Override
	public Plugin plugin() {
		return null;
	}

	@Override
	public void enable() {}

	@Override
	public String key() {
		return name().toLowerCase();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Map<IEnchantment, Integer> enchantments(ItemStack item) {
		Map<IEnchantment, Integer> map = new HashMap<>();
		if(ItemRegistry.nulled(item) == true || item.hasItemMeta() == false) return map;
		
		IMetaFetcher fetcher = EnchantmentRegistry.fetcher(item.getItemMeta());

		fetcher.enchantments().forEach((e, level) -> {
			NamespacedKey ns = e.getKey();
			String key = ns.getKey();
			if(ns.getNamespace().equals("enchantmentplus") == false) return;
			
			TextComponent t = new TextComponent();
			t.addExtra(new TranslatableComponent(e.getTranslationKey()));
			String name = t.toPlainText();
			
			int max = e.getMaxLevel();
			map.put(new EnchantmentPlus(e, key, name, max), level);
		});
		return map;
	}
	
	public record EnchantmentPlus(Enchantment enchantment, String key, String name, int maximum) implements IEnchantment {

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
