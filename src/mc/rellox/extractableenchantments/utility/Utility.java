package mc.rellox.extractableenchantments.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipe;
import mc.rellox.extractableenchantments.utility.Version.VersionType;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public final class Utility {
	
	private static final Random random = new Random();
	
	public static int random() {
		return random.nextInt();
	}
	
	public static int random(int a) {
		return random.nextInt(a);
	}
	
	public static int between(int a, int b) {
		return random.nextInt(b - a + 1) + a;
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static void update(IRecipe recipe) {
		if(recipe == null) return;
		boolean removed = false;
		try {
			removed = RF.order(Bukkit.getServer(), "removeRecipe", false, NamespacedKey.class).as(boolean.class)
					.invoke(false, recipe.namespace());
		} catch (Exception e) {}
		if(removed == false) {
			Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
			while(it.hasNext() == true) {
				Recipe r = it.next();
				if(r instanceof ShapedRecipe shaped) {
					if(shaped.getKey().equals(recipe.namespace()) == false) continue;
					it.remove();
					break;
				}
			}
		}
		Bukkit.addRecipe(recipe.recipe());
	}
	
	public static void tooltip(ItemMeta meta, String tooltip) {
		if(tooltip == null || Version.version.high(VersionType.v_21_2) == false) return;
		meta.setTooltipStyle(NamespacedKey.minecraft(tooltip));
	}

	public static void check(final int id, final Consumer<String> action) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try(InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id).openStream();
						Scanner sc = new Scanner(is)) {
					if(sc.hasNext() == true) action.accept(sc.next());
				} catch(IOException x) {}
			}
		}.runTaskLaterAsynchronously(ExtractableEnchantments.instance(), 50);
	}

}
