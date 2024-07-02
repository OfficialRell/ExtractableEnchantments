package mc.rellox.extractableenchantments.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.utility.Version.VersionType;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public final class Utility {
	
	private static final Random R = new Random();
	
	public static int random() {
		return R.nextInt();
	}
	
	public static int random(int a) {
		return R.nextInt(a);
	}
	
	public static int between(int a, int b) {
		return R.nextInt(b - a + 1) + a;
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
	
	public static void update(CraftingRecipe recipe) {
		if(recipe == null) return;
		boolean removed = false;
		try {
			removed = RF.order(Bukkit.getServer(), "removeRecipe", false, NamespacedKey.class).as(boolean.class)
					.invoke(false, recipe.getKey());
		} catch (Exception e) {}
		if(removed == false) {
			Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
			while(it.hasNext() == true) {
				Recipe r = it.next();
				if(r instanceof CraftingRecipe craft) {
					if(craft.getKey().equals(recipe.getKey()) == false) continue;
					it.remove();
					break;
				}
			}
		}
		Bukkit.addRecipe(recipe);
	}
	
	public static Class<?> craft(String s) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + Version.server + "." + s);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public static Class<?> nms(String s) {
		try {
			return Class.forName("net.minecraft.server." + Version.server + "." + s);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Material getMaterial(String name, Material def) {
		Material m = null;
		try {
			m = Material.valueOf(name);
			if(m == Material.AIR) return null;
		} catch(Exception e) {}
		return m == null ? def : m;
	}

	public static Material material(String name) {
		try {
			return Material.valueOf(name);
		} catch(Exception e) {}
		return null;
	}

	public static ChatColor getColor(String name, ChatColor def) {
		ChatColor m = null;
		try {
			m = ChatColor.valueOf(name);
		} catch(Exception e) {}
		return m == null ? def : m;
	}

	public static double round(double d) {
		return (double) ((int) (d * 100) / 100.0);
	}
	
	public static boolean isKey(String key) {
		for(char c : key.toCharArray()) {
			if(c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_') continue;
			return false;
		}
		return true;
	}
	
	public static String displayName(Material material) {
		return displayName(new ItemStack(material));
	}
	
	public static String displayName(ItemStack item) {
		try {
			Class<?> clazz = RF.craft("inventory.CraftItemStack");
			Object nms_item = RF.order(clazz, "asNMSCopy", ItemStack.class).invoke(item);
			String a, b = "getString";
			
			if(Version.version == VersionType.v_18_1) {
				a = "v";
				b = "a";
			} else if(Version.version == VersionType.v_18_2) {
				a = "w";
				b = "a";
			} else if(Version.version == VersionType.v_19_1
					|| Version.version == VersionType.v_19_2
					|| Version.version == VersionType.v_19_3) {
				a ="x";
			} else if(Version.version == VersionType.v_20_1
					|| Version.version == VersionType.v_20_2
					|| Version.version == VersionType.v_20_3) {
				a = "y";
			} else if(Version.version == VersionType.v_20_4) {
				a = "x";
			} else if(Version.version == VersionType.v_21_1) {
				a = "w";
			} else {
				a = "getName";
				b = "getText";
			}
			
			Object component = RF.direct(nms_item, a);
			String name = RF.direct(component, b, String.class);
			
			if(name == null)
				Bukkit.getLogger().warning("Null name got returned when trying to fetch item name");
			
			return ChatColor.stripColor(name);
		} catch(Exception e) {
			Bukkit.getLogger().warning("Cannot get item display name");
			return "null";
		}
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
