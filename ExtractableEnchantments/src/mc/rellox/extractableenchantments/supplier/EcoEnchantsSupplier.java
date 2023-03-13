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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.willfp.ecoenchants.EcoEnchantsPlugin;

import mc.rellox.extractableenchantments.configuration.Configuration;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.ExtractionType;
import mc.rellox.extractableenchantments.utils.Utils;

public class EcoEnchantsSupplier implements ESupplier<EcoEnchantsPlugin, Enchantment> {
	
	private EcoEnchantsPlugin plugin;
	private EcoEnchantsVersion version;
	
	@Override
	public EcoEnchantsPlugin get() {
		return this.plugin;
	}

	@Override
	public void load() {
		this.plugin = load0();
		try {
			Class.forName("com.willfp.ecoenchants.display.EnchantmentCache");
			this.version = new EcoEnchantsVersionOld();
		} catch (Exception e) {
			this.version = new EcoEnchantsVersionNew();
		}
	}
	
	@Override
	public boolean isEnchantment(Enchantment e) {
		return version.isEnchantment(e);
	}
	
	@Override
	public String name(Enchantment e) {
		return version.name(e);
	}

	@Override
	public void extract(Extractor ex, Player player, ItemStack item, Enchantment removed, int level,
			boolean failed, boolean storage) {
		try {
			if(failed == true) {
				if(ex.extraction == ExtractionType.RANDOM) player.setItemOnCursor(null);
				if(ex.chance_destroy == true) {
					if(storage == true) {
						EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
						meta.removeStoredEnchant(removed);
						item.setItemMeta(meta);
					} else {
						ItemMeta meta = item.getItemMeta();
						meta.removeEnchant(removed);
						item.setItemMeta(meta);
					}
					player.sendMessage(Language.exteaction_destroy_custom(removed, level));
					ExtractorRegistry.playOnFail(player, true);
				} else {
					player.sendMessage(Language.extraction_fail_custom(removed));
					ExtractorRegistry.playOnFail(player, false);
				}
			} else {
				if(storage == true) {
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
					meta.removeStoredEnchant(removed);
					item.setItemMeta(meta);
				} else {
					ItemMeta meta = item.getItemMeta();
					meta.removeEnchant(removed);
					item.setItemMeta(meta);
				}
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta book_meta = (EnchantmentStorageMeta) book.getItemMeta();
				book_meta.addStoredEnchant(removed, level, true);
				if(Configuration.book_chance_toggle() == true) {
					int chance = ex.book_chance_force == true ? ex.book_chance_value : Configuration.book_chance_random();
					List<String> lore = new ArrayList<>(2);
					lore.add("");
					lore.add(Language.book_lore_chance(chance));
					book_meta.setLore(lore);
					PersistentDataContainer p = book_meta.getPersistentDataContainer();
					p.set(DustRegistry.key_chance, PersistentDataType.INTEGER, chance);
				}
				book.setItemMeta(book_meta);
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

	private static EcoEnchantsPlugin load0() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("EcoEnchants");
		return (EcoEnchantsPlugin) plugin;
	}
	
	public static interface EcoEnchantsVersion {
		boolean isEnchantment(Enchantment e);
		String name(Enchantment e);
	}

}
