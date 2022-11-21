package mc.rellox.extractableenchantments.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.extractor.ExtractorLayout;
import mc.rellox.extractableenchantments.usage.Cost;
import mc.rellox.extractableenchantments.utils.Utils;
import mc.rellox.extractableenchantments.utils.Version;
import mc.rellox.extractableenchantments.utils.Version.VersionType;

public final class Language {

	private static File f;
	private static FileConfiguration file;

	private static final Map<String, String> ENCHANTMENT_NAMES = new HashMap<>();
	
	private static String cost_experience_points_insufficient;
	private static String cost_experience_points_amount;
	private static String cost_experience_levels_insufficient;
	private static String cost_experience_levels_amount;
	private static String cost_material_insufficient;
	private static String cost_material_amount;
	private static String cost_economy_insufficient;
	private static String cost_economy_amount;
	
	private static String permission_warn_craft;
	private static String permission_warn_use;
	private static String permission_warn_apply;
	private static String permission_warn_dust;
	private static String permission_warn_split;
	
	private static String extractor_lore_chance;
	private static String extractor_lore_cost;
	private static String extractor_lore_destroy;
	private static String extractor_selection_name;
	private static String extractor_selection_enchantment_name;
	private static String extractor_selection_enchantment_info;
	
	private static String extraction_succeed;
	private static String extraction_fail;
	private static String extraction_unsafe;
	private static String extraction_contraint;
	private static String extraction_extract_minecraft;
	private static String extraction_extract_custom;
	private static String exteaction_destroy;
	
	private static String book_lore_chance;
	private static String book_apply_fail;

	private static String dust_split_held;
	private static String dust_split_amount;
	private static String dust_split_less;
	private static String dust_split_small;
	
	private static ChatColor color_normal, color_curse;
	
	private static ExtractorLayout item_layout;
	
	public static void initialize() {
		initializeConfig();
		update();
	}
	
	private static void update() {
		try {
			cost_experience_points_insufficient = t(file.getString("Cost.ExperiencePoints.NotEnough"));
			cost_experience_points_amount = t(file.getString("Cost.ExperiencePoints.Amount"));
			cost_experience_levels_insufficient = t(file.getString("Cost.ExperienceLevels.NotEnough"));
			cost_experience_levels_amount = t(file.getString("Cost.ExperienceLevels.Amount"));
			cost_material_insufficient = t(file.getString("Cost.Material.NotEnough"));
			cost_material_amount = t(file.getString("Cost.Material.Amount"));
			cost_economy_insufficient = t(file.getString("Cost.Economy.NotEnough"));
			cost_economy_amount = t(file.getString("Cost.Economy.Amount"));
			
			permission_warn_craft = t(file.getString("Permission.Warn.Craft"));
			permission_warn_use = t(file.getString("Permission.Warn.Use"));
			permission_warn_apply = t(file.getString("Permission.Warn.Apply"));
			permission_warn_dust = t(file.getString("Permission.Warn.Dust"));
			permission_warn_split = t(file.getString("Permission.Warn.Split"));
			
			extractor_lore_chance = t(file.getString("Extractor.Lore.Chance"));
			extractor_lore_cost = t(file.getString("Extractor.Lore.Cost"));
			extractor_lore_destroy = t(file.getString("Extractor.Lore.Destroy"));
			extractor_selection_name = t(file.getString("Extration.Selection.Name"));
			extractor_selection_enchantment_name = t(file.getString("Extration.Selection.Enchantment.Name"));
			extractor_selection_enchantment_info = t(file.getString("Extration.Selection.Enchantment.Info"));
			
			extraction_succeed = t(file.getString("Extraction.Succeed"));
			extraction_fail = t(file.getString("Extraction.Fail"));
			extraction_contraint = t(file.getString("Extraction.Constraint"));
			extraction_unsafe = t(file.getString("Extraction.Unsafe"));
			extraction_extract_minecraft = t(file.getString("Extraction.Extract.Minecraft"));
			extraction_extract_custom = t(file.getString("Extraction.Extract.Custom"));
			exteaction_destroy = t(file.getString("Extraction.Destroy"));
			
			book_lore_chance = t(file.getString("Book.Lore.Chance"));
			book_apply_fail = t(file.getString("Book.Apply.Fail"));
			
			dust_split_held = t(file.getString("Dust.Split.Held"));
			dust_split_amount = t(file.getString("Dust.Split.Amount"));
			dust_split_less = t(file.getString("Dust.Split.Less"));
			dust_split_small = t(file.getString("Dust.Split.Small"));
			
			color_normal = Utils.getColor(file.getString("Enchantment.Color.Normal"), ChatColor.AQUA);
			color_curse = Utils.getColor(file.getString("Enchantment.Color.Curse"), ChatColor.RED);

			ENCHANTMENT_NAMES.clear();
			for(Enchantment ench : Enchantment.values()) {
				String name = file.getString("Enchantments." + ench.getKey().getKey().toUpperCase());
				ENCHANTMENT_NAMES.put(ench.getKey().getKey(), name == null ? "Missing" : name);
			}
			List<String> layout = file.getStringList("Item.Layout");
			item_layout = new ExtractorLayout(layout);
			
			// free memory
			f = null;
			file = null;
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
					+ ChatColor.DARK_RED + "An error accured while loading language file! "
							+ "All messages were not loaded! Check lang.yml to fix it!");
		}
	}

	public static String getName(Enchantment e) {
		return color(e) + ENCHANTMENT_NAMES.getOrDefault(e.getKey().getKey(), name(e));
	}

	public static String getName(Enchantment e, int level) {
		String name = color(e) + ENCHANTMENT_NAMES.getOrDefault(e.getKey().getKey(), name(e));
		return e.getMaxLevel() > 1 ? name + " " + roman(level) : name;
	}

	public static String getByName(Enchantment e, int level) {
		String name;
		if(ExtractableEnchantments.ECO_ENCHANTS.get() != null) 
			name = ChatColor.AQUA + ExtractableEnchantments.ECO_ENCHANTS.name(e);
		else name = ChatColor.AQUA + name(e);
		return e.getMaxLevel() > 1 ? name + " " + roman(level) : name;
	}
	
	@SuppressWarnings("deprecation")
	private static String name(Enchantment e) {
		String[] all = e.getName().toLowerCase().split("_");
		String name = "";
		for(String a : all) {
			if(a.equalsIgnoreCase("of") == true) name += " of";
			else name += " " + Character.toUpperCase(a.charAt(0)) + a.substring(1);
		}
		return name.substring(1);
	}

	public static String cost_experience_points_insufficient() {
		return cost_experience_points_insufficient;
	}
	
	public static String cost_experience_points_amount(int v) {
		return cost_experience_points_amount.replace("%amount%", "" + v);
	}
	
	public static String cost_experience_levels_insufficient() {
		return cost_experience_levels_insufficient;
	}
	
	public static String cost_experience_levels_amount(int v) {
		return cost_experience_levels_amount.replace("%amount%", "" + v);
	}
	
	public static String cost_material_insufficient() {
		return cost_material_insufficient;
	}
	
	public static String cost_material_amount(int v, Material m) {
		return cost_material_amount.replace("%amount%", "" + v).replace("%material_name%", Utils.displayName(m));
	}
	
	public static String cost_economy_insufficient() {
		return cost_economy_insufficient;
	}
	
	public static String cost_economy_amount(double v) {
		return cost_economy_amount.replace("%amount%", "" + v);
	}
	
	public static String permission_warn_craft() {
		return permission_warn_craft;
	}
	
	public static String permission_warn_use() {
		return permission_warn_use;
	}
	
	public static String permission_warn_apply() {
		return permission_warn_apply;
	}
	
	public static String permission_warn_dust() {
		return permission_warn_dust;
	}
	
	public static String permission_warn_split() {
		return permission_warn_split;
	}

	public static String extractor_lore_chance(int c) {
		return extractor_lore_chance.replace("%chance%", "" + c);
	}

	public static String extractor_lore_chance(String s) {
		return extractor_lore_chance.replace("%chance%", s);
	}

	public static String extractor_lore_cost(Cost c) {
		return extractor_lore_cost.replace("%cost%", c.cost());
	}

	public static String extractor_lore_destroy() {
		return extractor_lore_destroy;
	}

	public static String extractor_selection_name() {
		return extractor_selection_name;
	}

	public static String extractor_selection_name(Enchantment e, int l) {
		return extractor_selection_enchantment_name.replace("%enchant%", getName(e, l));
	}

	public static String extractor_selection_name_custom(Enchantment e, int l) {
		return extractor_selection_enchantment_name.replace("%enchant%", getByName(e, l));
	}

	public static String extractor_selection_info() {
		return extractor_selection_enchantment_info;
	}

	public static String book_lore_chance(int c) {
		return book_lore_chance.replace("%chance%", "" + c);
	}

	public static String extraction_succeed(Enchantment e, int level) {
		return extraction_succeed.replace("%enchant%", getName(e, level));
	}

	public static String extraction_succeed_custom(Enchantment e, int level) {
		return extraction_succeed.replace("%enchant%", getByName(e, level));
	}

	public static String extraction_fail(Enchantment e) {
		return extraction_fail.replace("%enchant%", getName(e));
	}

	@SuppressWarnings("deprecation")
	public static String extraction_fail_custom(Enchantment e) {
		String name;
		if(ExtractableEnchantments.ECO_ENCHANTS.get() != null) 
			name = ExtractableEnchantments.ECO_ENCHANTS.name(e);
		else name = e.getName();
		return extraction_fail.replace("%enchant%", name);
	}

	public static String extraction_unsafe() {
		return extraction_unsafe;
	}

	public static String extraction_contraint() {
		return extraction_contraint;
	}

	public static String extraction_extract_minecraft() {
		return extraction_extract_minecraft;
	}

	public static String extraction_extract_custom() {
		return extraction_extract_custom;
	}

	public static String exteaction_destroy(Enchantment e, int level) {
		return exteaction_destroy.replace("%enchant%", getName(e, level));
	}

	public static String exteaction_destroy_custom(Enchantment e, int level) {
		return exteaction_destroy.replace("%enchant%", getByName(e, level));
	}

	public static String book_apply_fail() {
		return book_apply_fail;
	}

	public static String dust_split_held() {
		return dust_split_held;
	}

	public static String dust_split_amount(int a) {
		return dust_split_amount("" + a);
	}

	public static String dust_split_amount(String amount) {
		return dust_split_amount.replace("%amount%", amount);
	}

	public static String dust_split_less(int limit) {
		return dust_split_less.replace("%limit%", "" + limit);
	}

	public static String dust_split_small() {
		return dust_split_small;
	}
	
	public static void chance_extractor_set(ItemStack item, int chance) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if(lore == null) return;
		String[] ss = extractor_lore_chance.split("%chance%");
		if(ss.length != 2) return;
		ListIterator<String> it = lore.listIterator();
		while(it.hasNext() == true) {
			String line = it.next();
			if(line.startsWith(ss[0]) == true
					&& line.endsWith(ss[1]) == true) {
				it.set(extractor_lore_chance(chance));
				meta.setLore(lore);
				item.setItemMeta(meta);
				return;
			}
		}
	}
	
	public static void chance_book_set(ItemStack item, int chance) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if(lore == null) return;
		String[] ss = book_lore_chance.split("%chance%");
		if(ss.length != 2) return;
		ListIterator<String> it = lore.listIterator();
		while(it.hasNext() == true) {
			String line = it.next();
			if(line.startsWith(ss[0]) == true
					&& line.endsWith(ss[1]) == true) {
				it.set(book_lore_chance(chance));
				meta.setLore(lore);
				item.setItemMeta(meta);
				return;
			}
		}
	}
	
	public static ExtractorLayout item_layout() {
		return item_layout;
	}
	
	public static ChatColor color(Enchantment e) {
		return e.getKey().equals(Enchantment.VANISHING_CURSE.getKey()) == true
				|| e.getKey().equals(Enchantment.BINDING_CURSE.getKey()) == true
				? color_curse : color_normal;
	}
	
	private static void initializeConfig() {
		f = new File(ExtractableEnchantments.instance().getDataFolder(), "lang.yml");
		if(f.getParentFile().exists() == false) f.getParentFile().mkdirs();
		if(f.exists() == true) file = YamlConfiguration.loadConfiguration(f);
		try {
			f.createNewFile();
		} catch(IOException e) {}
		file = YamlConfiguration.loadConfiguration(f);
		sd(Enchantment.ARROW_DAMAGE, "Power");
		sd(Enchantment.ARROW_FIRE, "Flame");
		sd(Enchantment.ARROW_INFINITE, "Infinity");
		sd(Enchantment.ARROW_KNOCKBACK, "Punch");
		sd(Enchantment.BINDING_CURSE, "Curse of Binding");
		sd(Enchantment.CHANNELING, "Channeling");
		sd(Enchantment.DAMAGE_ALL, "Sharpness");
		sd(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
		sd(Enchantment.DAMAGE_UNDEAD, "Smite");
		sd(Enchantment.DEPTH_STRIDER, "Depth Strider");
		sd(Enchantment.DIG_SPEED, "Efficiency");
		sd(Enchantment.DURABILITY, "Unbreaking");
		sd(Enchantment.FIRE_ASPECT, "Fire Aspect");
		sd(Enchantment.FROST_WALKER, "Frost Walker");
		sd(Enchantment.IMPALING, "Impaling");
		sd(Enchantment.KNOCKBACK, "Knockback");
		sd(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
		sd(Enchantment.LOOT_BONUS_MOBS, "Looting");
		sd(Enchantment.LOYALTY, "Loyalty");
		sd(Enchantment.LUCK, "Luck of the Sea");
		sd(Enchantment.LURE, "Lure");
		sd(Enchantment.MENDING, "Mending");
		sd(Enchantment.OXYGEN, "Respiration");
		sd(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
		sd(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
		sd(Enchantment.PROTECTION_FALL, "Feather Falling");
		sd(Enchantment.PROTECTION_FIRE, "Fire Protection");
		sd(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
		sd(Enchantment.RIPTIDE, "Riptide");
		sd(Enchantment.SILK_TOUCH, "Silk Touch");
		sd(Enchantment.SWEEPING_EDGE, "Sweeping Edge");
		sd(Enchantment.THORNS, "Thorns");
		sd(Enchantment.VANISHING_CURSE, "Curse of Vanishing");
		sd(Enchantment.WATER_WORKER, "Aqua Affinity");
		sd(Enchantment.getByKey(NamespacedKey.minecraft("swift_sneak")), "Swift Sneak");
		Enchantment e;
		if((e = getByName("MULTISHOT")) != null) sd(e, "Multishot");
		if((e = getByName("PIERCING")) != null) sd(e, "Piercing");
		if((e = getByName("QUICK_CHARGE")) != null) sd(e, "Quick Charge");
		if((e = getByName("SOUL_SPEED")) != null) sd(e, "Soul Speed");
		file.addDefault("Cost.ExperiencePoints.NotEnough", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Not enough experience points!"));
		file.addDefault("Cost.ExperiencePoints.Amount", a("%amount% Experience Points"));
		file.addDefault("Cost.ExperienceLevels.NotEnough", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Not enough experience levels!"));
		file.addDefault("Cost.ExperienceLevels.Amount", a("%amount% Experience Levels"));
		file.addDefault("Cost.Material.NotEnough", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Not enough materials!"));
		file.addDefault("Cost.Material.Amount", a("%amount% × %material_name%"));
		file.addDefault("Cost.Economy.NotEnough", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Insufficient funds!"));
		file.addDefault("Cost.Economy.Amount", a("$%amount%"));

		file.addDefault("Permission.Warn.Craft", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "You do not have a permission to craft this!"));
		file.addDefault("Permission.Warn.Use", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "You do not have a permission to use this!"));
		file.addDefault("Permission.Warn.Apply", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "You do not have a permission to apply this!"));
		file.addDefault("Permission.Warn.Dust", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "You do not have a permission to apply this!"));
		file.addDefault("Permission.Warn.Split", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "You do not have a permission to split this!"));

		file.addDefault("Extractor.Lore.Chance", a(ChatColor.GRAY + "Extraction Chance: " + ChatColor.BLUE + "%chance%%"));
		file.addDefault("Extractor.Lore.Cost", a(ChatColor.GRAY + "Extraction Cost: " + ChatColor.DARK_GREEN + "%cost%"));
		file.addDefault("Extractor.Lore.Destroy", a("" + ChatColor.DARK_RED + ChatColor.ITALIC + "Destroys enchantment on failure!"));
		file.addDefault("Book.Lore.Chance", a(ChatColor.GRAY + "Applying Chance: " + ChatColor.BLUE + "%chance%%"));
		file.addDefault("Extraction.Succeed", a(ChatColor.DARK_GREEN + "(!) " + ChatColor.GREEN + "Successfully extracted %enchant%"));
		file.addDefault("Extraction.Fail", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Extraction Failed!"));
		file.addDefault("Extraction.Unsafe", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Unable to extract unsafe enchantments!"));
		file.addDefault("Extraction.Constraint", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Cannot extract from this item!"));
		file.addDefault("Extraction.Extract.Minecraft", a(ChatColor.DARK_RED + "(!) "
				+ ChatColor.GOLD + "Unable to extract non-minecraft enchantments!"));
		file.addDefault("Extraction.Extract.Custom", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Unable to extract minecraft enchantments!"));
		file.addDefault("Extraction.Destroy", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Extraction Failed! Enchantment %enchant%"
				+ ChatColor.DARK_RED + " was destroyed!"));
		file.addDefault("Book.Apply.Fail", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Enchantment book failed to apply!"));
		file.addDefault("Extration.Selection.Name", a("Select an Enchantment"));
		file.addDefault("Extration.Selection.Enchantment.Name", a(ChatColor.DARK_PURPLE + "Enchantment: %enchant%"));
		file.addDefault("Extration.Selection.Enchantment.Info", a(ChatColor.DARK_GRAY + "» "
				+ ChatColor.GRAY + "Click to extract"));
		file.addDefault("Enchantment.Color.Normal", ChatColor.AQUA.name());
		file.addDefault("Enchantment.Color.Curse", ChatColor.RED.name());
		file.addDefault("Item.Layout", Arrays.asList("INFO", "EMPTY", "CHANCE", "DESTROY", "EMPTY", "COST"));
		file.addDefault("Dust.Split.Held", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Cannot split from held item!"));
		file.addDefault("Dust.Split.Amount", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Invalid amount ("
				+ ChatColor.YELLOW + "%amount%" + ChatColor.GOLD + ")!"));
		file.addDefault("Dust.Split.Less", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "You can only split less then "
				+ ChatColor.YELLOW + "%limit%" + ChatColor.GOLD + "!"));
		file.addDefault("Dust.Split.Small", a(ChatColor.DARK_RED + "(!) " + ChatColor.GOLD + "Cannot split this dust!"));
	    file.options().copyDefaults(true);
	    file.options().header("In this file you can change all text and messages.");
		save();
	}
	
	private static void sd(Enchantment e, String name) {
		if(e == null) return;
		file.addDefault("Enchantments." + e.getKey().getKey().toUpperCase(), name);
	}

	public static void save() {
		try {
			file.save(f);
		} catch(IOException e) {}
	}
	
	private static String roman(int i) {
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
	
	protected static String a(String s) {
		return s.replace('§', '&');
	}
	
	protected static void t(List<String> list) {
		if(list.isEmpty() == true) return;
		int i = 0;
		do list.set(i, t(list.get(i))); while(++i < list.size());
	}
	
	protected static String t(String s) {
		if(s == null || s.isEmpty() == true) return "";
		if(Version.version.high(VersionType.v_16_1) == true) {
			char[] a = s.toCharArray();
			StringBuilder b = new StringBuilder();
			for(int i = 0; i < a.length; i++) {
				if(a[i] == '[' && i + 1 < a.length && a[i + 1] == '#' && i + 6 < a.length) {
					String hex = "#" + a[++i + 1] + a[i + 2] + a[i + 3] + a[i + 4] + a[i + 5] + a[i + 6];
					Class<?> c = net.md_5.bungee.api.ChatColor.class;
					try {
						Method m = c.getMethod("of", String.class);
						b.append(m.invoke(null, hex));
						i += 7;
					} catch (Exception e) {}
				} else b.append(a[i]);
			}
			s = b.toString();
		}
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static Enchantment getByName(String name) {
		try {
			Field f = Enchantment.class.getField(name);
			return (Enchantment) f.get(null);
		} catch (Exception e) {
			return null;
		} 
	}
}
