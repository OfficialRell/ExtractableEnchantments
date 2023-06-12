package mc.rellox.extractableenchantments.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.utils.Version.VersionType;

public final class Utils {
	
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
	
	public static boolean isFull(Player player) {
		PlayerInventory pi = player.getInventory();
		for(int i = 0; i < 36; i++) if(isNull(pi.getItem(i)) == true) return false;
		return true;
	}

	public static int slots(Player player) {
		PlayerInventory pi = player.getInventory();
		int a = 0;
		for(int i = 0; i < 36; i++) if(isNull(pi.getItem(i)) == true) a++;
		return a;
	}

	public static boolean isNull(ItemStack item) {
		return item == null || item.getType() == Material.AIR;
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
	
	public static String displayName(Material m) {
		return displayName(new ItemStack(m));
	}
	
	public static String displayName(ItemStack item) {
		Class<?> c = craft("inventory.CraftItemStack");
		try {
			Method m = c.getMethod("asNMSCopy", ItemStack.class);
			Object o = m.invoke(null, item);
			c = o.getClass();
			if(Version.version == VersionType.v_18_1) {
				m = c.getDeclaredMethod("v");
				o = m.invoke(o);
				m = o.getClass().getMethod("a");
			} else if(Version.version == VersionType.v_18_2) {
				m = c.getDeclaredMethod("w");
				o = m.invoke(o);
				m = o.getClass().getMethod("a");
			} else if(Version.version == VersionType.v_19_1
					|| Version.version == VersionType.v_19_2
					|| Version.version == VersionType.v_19_3) {
				m = c.getDeclaredMethod("x");
				o = m.invoke(o);
				m = o.getClass().getMethod("getString");
			} else if(Version.version == VersionType.v_20_1) {
				m = c.getDeclaredMethod("y");
				o = m.invoke(o);
				m = o.getClass().getMethod("getString");
			} else {
				m = c.getDeclaredMethod("getName");
				o = m.invoke(o);
				m = o.getClass().getMethod("getText");
			}
			String name = (String) m.invoke(o);
			if(name == null || name.isEmpty() == true) {
				m = o.getClass().getMethod("getString");
				name = (String) m.invoke(o);
			}
			return name;
		} catch(Exception e) {
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
