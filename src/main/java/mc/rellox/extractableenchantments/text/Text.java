package mc.rellox.extractableenchantments.text;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.text.content.Content;
import mc.rellox.extractableenchantments.utility.Version;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public final class Text {
	
	public static final String symbol_multiplier = "" + '\u00D7';
	public static final char color_code = ChatColor.COLOR_CHAR;
	
	public static void logLoad() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "ExtractableEnchantments " + ChatColor.AQUA + "v"
				+ ExtractableEnchantments.PLUGIN_VERSION + ChatColor.DARK_PURPLE + "]" + ChatColor.GREEN + " enabled!");
	}
	
	public static void logUnload() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "ExtractableEnchantments " + ChatColor.AQUA + "v"
				+ ExtractableEnchantments.PLUGIN_VERSION + ChatColor.DARK_PURPLE + "]" + ChatColor.RED + " disabled!");
	}
	
	public static void logOutdated(double v) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Spawner Meta "
				+ ChatColor.AQUA + "v" + ExtractableEnchantments.PLUGIN_VERSION + ChatColor.DARK_PURPLE + "] "
				+ ChatColor.YELLOW + "New version is available: v" + v + "! " + ChatColor.GOLD + "To download visit: "
				+ "https://www.spigotmc.org/resources/ExtractableEnchantments.74188/");
	}
	
	public static void logInfo(String info) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "ExtractableEnchantments"
				+ ChatColor.DARK_PURPLE + "] " + ChatColor.GRAY + info);
	}
	
	public static void logFail(String fail) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "ExtractableEnchantments " + ChatColor.AQUA + "v"
				+ ExtractableEnchantments.PLUGIN_VERSION + ChatColor.DARK_PURPLE + "] " + ChatColor.DARK_RED + fail);
	}
	
	public static void success(String success, Object... os) {
		String w = ChatColor.DARK_GREEN + "(!) " + ChatColor.GREEN + success;
		if(os != null) for(int i = 0; i < os.length; i++) w = w.replace("#" + i, ChatColor.AQUA + os[i].toString() + ChatColor.GREEN);
		Bukkit.getConsoleSender().sendMessage(w);
	}
	
	public static void failure(String warning, Object... os) {
		String w = ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + warning;
		if(os != null) for(int i = 0; i < os.length; i++) w = w.replace("#" + i, ChatColor.YELLOW + os[i].toString() + ChatColor.GOLD);
		Bukkit.getConsoleSender().sendMessage(w);
	}
	
	public static List<String> toText(List<Content> list) {
		return list.stream()
				.map(Content::text)
				.collect(Collectors.toList());
	}

	public static String color(String hex) {
		if(hex == null || hex.isEmpty()) return "";
		if(hex.charAt(0) == '#') hex = hex.substring(1);
		String s = color_code + "x";
		for(char c : hex.toCharArray()) s += color_code + "" + c;
		return s;
	}
	
	public static String color(int rgb) {
		return color(String.format("%06x", rgb));
	}
	
	public static String roman(int i) {
		if(i <= 0 || i > 5000) return "" + i;
		StringBuilder sb = new StringBuilder();
		if(i >= 1000) do sb.append("M"); while((i -= 1000) >= 1000);
		if(i >= 900) i -= r(sb, "CM", 900);
		if(i >= 500) i -= r(sb, "D", 500);
		if(i >= 400) i -= r(sb, "CD", 400);
		if(i >= 100) do sb.append("C"); while((i -= 100) >= 100);
		if(i >= 90) i -= r(sb, "XC", 90);
		if(i >= 50) i -= r(sb, "L", 50);
		if(i >= 40) i -= r(sb, "XL", 40);
		if(i >= 10) do sb.append("X"); while((i -= 10) >= 10);
		if(i >= 9) i -= r(sb, "IX", 9);
		if(i >= 5) i -= r(sb, "V", 5);
		if(i >= 4) i -= r(sb, "IV", 4);
		if(i >= 1) do sb.append("I"); while(--i >= 1);
		return sb.toString();
	}
	
	private static int r(StringBuilder sb, String s, int i) {
		sb.append(s);
		return i;
	}
	
	public static String display(Material material) {
		return display(new ItemStack(material));
	}
	
	public static String display(ItemStack item) {
		try {
			Class<?> clazz = RF.craft("inventory.CraftItemStack");
			Object nms_item = RF.order(clazz, "asNMSCopy", ItemStack.class).invoke(item);

			String[] names = switch (Version.version) {
				case v_18_1 -> new String[] {"v", "a"};
				case v_18_2 -> new String[] {"w", "a"};
				case v_19_1, v_19_2, v_19_3, v_20_4 -> new String[] {"x", "getString"};
				case v_21_1 -> new String[] {"w", "getString"};
				default -> new String[] {"y", "getString"};
			};
			
			String a = names[0];
			String b = names[1];
			
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
	
	private static final Pattern key_pattern = Pattern.compile("[a-z_\\d]*");
	
	public static boolean key(String key) {
		return key_pattern.matcher(key).matches();
	}
	
	public static void clean(List<String> list) {
		boolean n = false;
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			if(it.next().isEmpty()) {
				if(n) it.remove();
				n = true;
			} else n = false;
		}
		if(!list.isEmpty()) {
			int last = list.size() - 1;
			if(list.get(last).isEmpty())
				list.remove(last);
		}
	}
	
	public static List<String> fromLegacy(List<String> list) {
		return list.stream()
				.map(Text::fromLegacy)
				.collect(Collectors.toList());
	}
	
	public static String fromLegacy(String s) {
		StringBuilder sb = new StringBuilder();
		boolean l = false, h = false, i = false;
		String x = "";
		for(char c : s.toCharArray()) {
			if(c == '<') i = true;
			else if(c == '&') l = true;
			else if(c == '#') h = true;
			else {
				if(l) {
					String o = switch (c) {
					case 'a' -> "<#00ff00>"; case 'b' -> "<#00ffff>";
					case 'c' -> "<#ff0000>"; case 'd' -> "<#ff00ff>";
					case 'e' -> "<#ffff00>"; case 'f' -> "<#ffffff>";
					case '1' -> "<#000080>"; case '2' -> "<#008000>";
					case '3' -> "<#008080>"; case '4' -> "<#800000>";
					case '5' -> "<#800080>"; case '6' -> "<#ff8000>";
					case '7' -> "<#c4c4c4>"; case '8' -> "<#595959>";
					case '9' -> "<#0000ff>"; case '0' -> "<#000000>";
					case 'k' -> "<!obfuscated>"; case 'l' -> "<!bold>";
					case 'm' -> "<!strikethrough>"; case 'n' -> "<!underline>";
					case 'o' -> "<!italic>"; default -> "";
					};
					sb.append(o);
					l = false;
				} else if(h) {
					x += "" + c;
					if(x.length() >= 6) {
						sb.append("<#").append(x).append('>');
						x = "";
						h = false;
					}
				} else sb.append(c);
			}
			if(i && l || h) return s;
			i = false;
		}
		return sb.toString();
	}
	
}
