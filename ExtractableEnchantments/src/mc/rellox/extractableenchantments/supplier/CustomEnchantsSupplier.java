package mc.rellox.extractableenchantments.supplier;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.ExtractionType;
import me.JayMar921.CustomEnchantment.CustomEnchantmentMain;
import me.JayMar921.CustomEnchantment.EnchantmentGetters;

public final class CustomEnchantsSupplier implements ESupplier<CustomEnchantmentMain, Enchantment> {

	private CustomEnchantmentMain custom_enchants;

	@Override
	public CustomEnchantmentMain get() {
		return this.custom_enchants;
	}

	@Override
	public void load() {
		custom_enchants = loadCustomEnchants();
	}

	@Override
	public boolean isEnchantment(Enchantment e) {
		Enchantment ee = new EnchantmentGetters().getEnchantment(convert(e));
		return ee == e;
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
			CommandExecutor executor = custom_enchants.getCommand("customenchantments").getExecutor();
			if(failed == true) {
				if(ex.extraction == ExtractionType.RANDOM) player.setItemOnCursor(null);
				if(ex.chance_destroy == true) {
					removeEnchantment(executor, player, item, convert(removed));
					player.sendMessage(Language.exteaction_destroy_custom(removed, level));
					ExtractorRegistry.playOnFail(player, true);
				} else {
					player.sendMessage(Language.extraction_fail_custom(removed));
					ExtractorRegistry.playOnFail(player, false);
				}
			} else {
				if(ex.extraction == ExtractionType.RANDOM) player.setItemOnCursor(null);
				removeEnchantment(executor, player, item, convert(removed));
				Method add_enchant = executor.getClass().getDeclaredMethod("givePlayer", Player.class, String.class);
				add_enchant.setAccessible(true);
				add_enchant.invoke(executor, player, convert(removed));
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
	
	private void removeEnchantment(CommandExecutor executor, Player player, ItemStack item, String name) {
		try {
			Method m = executor.getClass().getDeclaredMethod("removeEnchantment", Player.class, ItemStack.class, String.class);
			m.invoke(executor, player, item, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String convert(Enchantment e) {
		String key = e.getKey().getKey();
		if(key.equalsIgnoreCase("protectionce") == true) key = "Protection";
		if(key.equalsIgnoreCase("unbreakingce") == true) key = "Unbreaking";
		return key;
	}

	private static CustomEnchantmentMain loadCustomEnchants() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CustomEnchantments");
		return (CustomEnchantmentMain) plugin;
	}

}
