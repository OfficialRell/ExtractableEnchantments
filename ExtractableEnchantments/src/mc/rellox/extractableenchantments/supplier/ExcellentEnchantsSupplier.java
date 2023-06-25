package mc.rellox.extractableenchantments.supplier;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import mc.rellox.extractableenchantments.configuration.Configuration;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.ExtractionType;
import mc.rellox.extractableenchantments.utils.Utils;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;
import su.nightexpress.excellentenchants.enchantment.util.EnchantUtils;

public class ExcellentEnchantsSupplier implements ESupplier<Plugin, Enchantment> {
	
	private Plugin plugin;

	@Override
	public Plugin get() {
		return this.plugin;
	}

	@Override
	public void load() {
		this.plugin = load0();
	}

	@Override
	public boolean isEnchantment(Enchantment e) {
		return e instanceof ExcellentEnchant;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String name(Enchantment e) {
		return e.getName();
	}

	@Override
	public void extract(Extractor ex, Player player, ItemStack item, Enchantment removed, int level,
			boolean failed, boolean storage) {
		try {
			if(failed == true) {
				if(ex.extraction == ExtractionType.RANDOM) player.setItemOnCursor(null);
				if(ex.chance_destroy == true) {
					EnchantUtils.remove(item, removed);
					EnchantUtils.updateDisplay(item);
					player.sendMessage(Language.exteaction_destroy_custom(removed, level));
					ExtractorRegistry.playOnFail(player, true);
				} else {
					player.sendMessage(Language.extraction_fail_custom(removed));
					ExtractorRegistry.playOnFail(player, false);
				}
			} else {
				if(ex.extraction == ExtractionType.RANDOM) player.setItemOnCursor(null);
				EnchantUtils.remove(item, removed);
				EnchantUtils.updateDisplay(item);
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta book_meta = (EnchantmentStorageMeta) book.getItemMeta();
				if(Configuration.book_chance_toggle() == true) {
					int chance = ex.book_chance_force == true ? ex.book_chance_value : Configuration.book_chance_random();
					List<String> lore = new ArrayList<>();
					lore.add("");
					lore.add(Language.book_lore_chance(chance));
					book_meta.setLore(lore);
					PersistentDataContainer p = book_meta.getPersistentDataContainer();
					p.set(DustRegistry.key_chance, PersistentDataType.INTEGER, chance);
				}
				book.setItemMeta(book_meta);
				EnchantUtils.add(book, removed, level, true);
				EnchantUtils.updateDisplay(book);
				if(ex.extraction == ExtractionType.RANDOM) {
					if(player.getGameMode() == GameMode.CREATIVE) player.getInventory().addItem(book);
					else player.setItemOnCursor(book);
				}
				else {
					if(Utils.slots(player) <= 0) player.getWorld().dropItem(player.getLocation(), book);
					else player.getInventory().addItem(book);
				}
				player.sendMessage(Language.extraction_succeed_custom(removed, level));
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2f, 0f);
				player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 0f);
				DustOptions o = new DustOptions(Color.BLACK, 1f);
				player.spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1, 0), 50, 0.2, 0.5, 0.2, 0, o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Plugin load0() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ExcellentEnchants");
		return plugin;
	}

}
