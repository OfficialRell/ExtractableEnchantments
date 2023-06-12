package mc.rellox.extractableenchantments.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.dust.Dust;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.Extractor.RecipeItem;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.Constraint;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.Extract;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.ExtractionType;
import mc.rellox.extractableenchantments.extractor.Restriction;
import mc.rellox.extractableenchantments.usage.CostType;
import mc.rellox.extractableenchantments.utils.Utils;

public final class Configuration {
	
	private static File f;
	private static FileConfiguration file;

	public static float extraction_volume;
	
	private static boolean book_chance_toggle;
	private static int book_chance_min, book_chance_max;
	
	private static boolean anvil_apply_books, anvil_apply_unsafe;
	private static List<Restriction> restrictions;
	
	public static void initialize() {
		create();
	}

	private static void create() {
		f = new File(ExtractableEnchantments.instance().getDataFolder(), "config.yml");
		if(f.getParentFile().exists() == false) f.getParentFile().mkdirs(); 
		if(f.exists() == true) file = YamlConfiguration.loadConfiguration(f); 
		else {
			try {
				f.createNewFile();
			} catch (IOException e) {}
			file = YamlConfiguration.loadConfiguration(f);
		}
		
		String path = "Extractors.default";
		file.addDefault(path + ".Material", Material.NETHER_BRICK.name());
		file.addDefault(path + ".Name", Language.a(ChatColor.AQUA + "Enchantment Extractor"));
		file.addDefault(path + ".Info", List.of(
				Language.a(ChatColor.DARK_GRAY + "* " + ChatColor.DARK_AQUA + "Drag and drop onto an item"),
				Language.a(ChatColor.DARK_AQUA + "to remove a random enchantment!")));
		file.addDefault(path + ".Glint", true);
		file.addDefault(path + ".CustomModelData", 0);
		file.addDefault(path + ".Chance.Toggle", false);
	    file.addDefault(path + ".Chance.Destroy", false);
	    file.addDefault(path + ".Chance.Min", 1);
	    file.addDefault(path + ".Chance.Max", 100);
	    file.addDefault(path + ".Cost.Toggle", false);
	    file.addDefault(path + ".Cost.Type", CostType.EXPERIENCE_POINTS.name());
	    file.addDefault(path + ".Cost.Material", Material.GOLD_INGOT.name());
	    file.addDefault(path + ".Cost.Value", 32);
	    file.addDefault(path + ".Extract.Unsafe", true);
	    file.addDefault(path + ".IgnoredEnchantments", new ArrayList<>());
	    file.addDefault(path + ".Book.Chance.Force", false);
	    file.addDefault(path + ".Book.Chance.Value", 100);
	    file.addDefault(path + ".Extraction", ExtractionType.RANDOM.name());
	    file.addDefault(path + ".Filter", "ALL");
	    file.addDefault(path + ".Constraints", new ArrayList<String>());
	    file.addDefault(path + ".Recipe.Toggle", true);
	    List<String> recipe = new ArrayList<>(9);
	    recipe.add(Material.EXPERIENCE_BOTTLE.name());
	    recipe.add(Material.LAPIS_BLOCK.name());
	    recipe.add(Material.EXPERIENCE_BOTTLE.name());
	    recipe.add(Material.LAPIS_BLOCK.name());
	    recipe.add(Material.BOOK.name());
	    recipe.add(Material.LAPIS_BLOCK.name());
	    recipe.add(Material.EXPERIENCE_BOTTLE.name());
	    recipe.add(Material.LAPIS_BLOCK.name());
	    recipe.add(Material.EXPERIENCE_BOTTLE.name());
	    file.addDefault(path + ".Recipe.Matrix", recipe);
	    
		path = "Dusts.default";
		
		if(file.isList(path + ".Info") == true) {
			var l = file.getStringList(path + ".Info");
			boolean save = false;
			for(int i = 0; i < l.size(); i++) {
				String s = l.get(i);
				if(s.contains("Middle click") == false) continue;
				s = s.replace("Middle click", "Shift-right-click");
				l.set(i, s);
				save = true;
				break;
			}
			if(save == true) file.set(path + ".Info", l);
		}
		
		file.addDefault(path + ".Material", Material.SUGAR.name());
		file.addDefault(path + ".Name", Language.a(ChatColor.YELLOW + "Dust of Chance " + ChatColor.AQUA + "(%percent%%)"));
		file.addDefault(path + ".Info", List.of(
				Language.a(ChatColor.DARK_GRAY + "* " + ChatColor.GOLD + "Drag and drop onto an extractor"),
				Language.a(ChatColor.GOLD + "to increase its extraction chance!"),
				"",
				Language.a(ChatColor.DARK_GRAY + "* " + ChatColor.GRAY + "Shift-right-click to split in half.")));
		file.addDefault(path + ".Glint", true);
		file.addDefault(path + ".CustomModelData", 0);
		file.addDefault(path + ".Allowed", List.of("default"));
		file.addDefault(path + ".Stackable", false);
		file.addDefault(path + ".Books", false);
		file.addDefault(path + ".Limit", -1);
	    file.addDefault(path + ".Recipe.Toggle", false);
	    file.addDefault(path + ".Recipe.Percent", 10);
	    recipe = new ArrayList<>(9);
	    recipe.add("EMPTY");
	    recipe.add(Material.REDSTONE.name());
	    recipe.add("EMPTY");
	    recipe.add(Material.REDSTONE.name());
	    recipe.add(Material.GLOWSTONE.name());
	    recipe.add(Material.REDSTONE.name());
	    recipe.add("EMPTY");
	    recipe.add(Material.REDSTONE.name());
	    recipe.add("EMPTY");
	    file.addDefault(path + ".Recipe.Matrix", recipe);
	    
	    file.addDefault("Settings.Sounds.ExtractionVolume", 1.0);
	    file.addDefault("Settings.Items.CanStack", false);
	    
	    file.addDefault("Book.Chance.Toggle", false);
	    file.addDefault("Book.Chance.Min", 1);
	    file.addDefault("Book.Chance.Max", 100);
	    file.addDefault("Anvil.Apply.Books", true);
	    file.addDefault("Anvil.Apply.Unfase", false);
	    file.addDefault("Anvil.Apply.Restrict", new ArrayList<>());
	    file.options().copyDefaults(true);
	    file.options().header("In this file you can configure extractors, dust and books.\n"
	    		+ "For more information about configuration visit:\n"
	    		+ "   https://www.spigotmc.org/resources/extractable-enchantments.73954/");
		save();
		
		extraction_volume = (float) file.getDouble("Settings.Sounds.ExtractionVolume");
		
		book_chance_toggle = file.getBoolean("Book.Chance.Toggle", false);
		book_chance_min = file.getInt("Book.Chance.Min", 1);
		book_chance_max = file.getInt("Book.Chance.Max", 100);
		
		anvil_apply_books = file.getBoolean("Anvil.Apply.Books", true);
		anvil_apply_unsafe = file.getBoolean("Anvil.Apply.Unfase", false);
		List<String> list = file.getStringList("Anvil.Apply.Restrict");
		restrictions = new LinkedList<>();
		if(list != null && list.isEmpty() == false) {
			Restriction r;
			for(String s : list) if((r = Restriction.of(s)) != null) restrictions.add(r);
		}
	}
	
	public static boolean book_chance_toggle() {
		return book_chance_toggle;
	}
	
	public static int book_chance_random() {
		return Utils.between(book_chance_min, book_chance_max);
	}
	
	public static boolean anvil_apply_books() {
		return anvil_apply_books;
	}
	
	public static boolean anvil_apply_unsafe() {
		return anvil_apply_unsafe;
	}
	
	public static boolean restricted(Material what, Material to) {
		if(restrictions.isEmpty() == true) return false;
		for(Restriction r : restrictions) if(r.restricted(what, to) == true) return true;
		return false;
	}
	
	public static List<Extractor> extractors() {
		List<Extractor> extractors = new LinkedList<>();
		ConfigurationSection section = file.getConfigurationSection("Extractors");
		if(section != null) {
			Set<String> keys = section.getKeys(false);
			if(keys.isEmpty() == false) {
				for(String key : keys) {
					if(Utils.isKey(key) == false) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
								+ ChatColor.DARK_RED + "Unable to load extractor, invalid extractor key (" + key + ").");
						continue;
					}
					String path = "Extractors." + key;
					Material material = Utils.getMaterial(file.getString(path + ".Material"), Material.NETHER_BRICK);
					String name = Language.t(file.getString(path + ".Name"));
					List<String> info = file.getStringList(path + ".Info");
					Language.t(info);
					boolean glint = file.getBoolean(path + ".Glint", true);
					int model = file.getInt(path + ".CustomModelData", 1);
					if(model <= 0) model = 1;
					boolean chance_toggle = file.getBoolean(path + ".Chance.Toggle", false);
					boolean chance_destroy = file.getBoolean(path + ".Chance.Destroy", false);
					int chance_min = file.getInt(path + ".Chance.Min", 1);
					int chance_max = file.getInt(path + ".Chance.Max", 100);
					if(chance_min < 0) chance_min = 0;
					chance_max = Math.min(chance_max, 100);
					if(chance_min > chance_max) chance_min = chance_max;
					boolean cost_toggle = file.getBoolean(path + ".Cost.Toggle", false);
					CostType cost_type = CostType.of(file.getString(path + ".Cost.Type"));
					Material cost_material = Utils.getMaterial(file.getString(path + ".Cost.Material"), Material.GOLD_INGOT);
					int cost_value = file.getInt(path + ".Cost.Value", 100);
					if(cost_value <= 0) cost_value = 16;
					boolean book_chance_force = file.getBoolean(path + ".Book.Chance.Force", false);
					int book_chance_value = file.getInt(path + ".Book.Chance.Value", 100);
					if(book_chance_value < 0) book_chance_value = 0;
					else if(book_chance_value > 100) book_chance_value = 100;
					boolean extract_unsafe = file.getBoolean(path + ".Extract.Unsafe", true);
					List<String> ignored_enchantments = file.getStringList(path + ".IgnoredEnchantments");
					boolean stackable = file.getBoolean(path + ".Stackable", false);
					ExtractionType extraction = ExtractionType.of(file.getString(path + ".Extraction"));
					Extract extract = Extract.of(file.getString(path + ".Filter"));
					List<String> list = file.getStringList(path + ".Constraints");
					Constraint[] cs;
					if(list.isEmpty() == false) {
						Constraint c;
						List<Constraint> cl = new ArrayList<>(list.size());
						for(String s : list) if((c = Constraint.of(s)) != null) cl.add(c);
						cs = cl.toArray(new Constraint[0]);
					} else cs = null;
					boolean recipe_toggle = file.getBoolean(path + ".Recipe.Toggle", true);
					RecipeItem[] recipe_matrix = new RecipeItem[9];
					list = file.getStringList(path + ".Recipe.Matrix");
					if(list.size() == 9) {
						int e = 0;
						for(int i = 0; i < 9; i++) {
							String s = list.get(i);
							if(s.matches("[a-zA-z_]*:\\d*") == true) {
								String[] ss = s.split(":");
								Material m = Utils.getMaterial(ss[0], null);
								if(m == null) e++;
								else {
									if(Utils.isInteger(ss[1]) == false) e++;
									else recipe_matrix[i] = new RecipeItem(m, Integer.parseInt(ss[1])); 
								}
							} else {
								Material m = Utils.getMaterial(s, null);
								if(m == null) e++;
								else recipe_matrix[i] = new RecipeItem(m, 1); 
							}
						}
						if(e == 9) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
									+ ChatColor.RED + "Unable to load recipe for extractor (" + key + "), recipe cannot be empty!");
						}
					} else if(list.isEmpty() == false) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
								+ ChatColor.RED + "Unable to load recipe for extractor (" + key + "), recipe size can only be 9!");
					}
					Extractor extractor = new Extractor(key, material, name, info, glint, model, chance_toggle, chance_destroy,
							chance_min, chance_max, cost_toggle, cost_type, cost_material, cost_value,
							book_chance_force, book_chance_value, extract_unsafe, ignored_enchantments,
							stackable, extraction, extract, cs, recipe_toggle, recipe_matrix);
					extractors.add(extractor);
				}
			}
		}
		return extractors;
	}
	
	public static void updateRecipe(ShapedRecipe recipe) {
		if(recipe != null) {
			boolean b = false;
			try {
				Server s = Bukkit.getServer();
				Method m = s.getClass().getMethod("removeRecipe", NamespacedKey.class);
				b = (boolean) m.invoke(s, recipe.getKey());
			} catch (Exception e) {}
			if(b == false) {
				Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
				while(it.hasNext() == true) {
					Recipe r = it.next();
					if(r instanceof ShapedRecipe sr) {
						if(sr.getKey().equals(recipe.getKey()) == true) {
							it.remove();
							break;
						}
					}
				}
			}
			Bukkit.addRecipe(recipe);
		}
	}
	
	public static List<Dust> dusts() {
		List<Dust> dusts = new LinkedList<>();
		ConfigurationSection section = file.getConfigurationSection("Dusts");
		if(section != null) {
			Set<String> keys = section.getKeys(false);
			if(keys.isEmpty() == false) {
				for(String key : keys) {
					if(Utils.isKey(key) == false) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
								+ ChatColor.DARK_RED + "Unable to load dust, invalid dust key (" + key + ").");
						continue;
					}
					String path = "Dusts." + key;
					Material material = Utils.getMaterial(file.getString(path + ".Material"), Material.SUGAR);
					String name = Language.t(file.getString(path + ".Name"));
					if(name.contains("%percent%") == false) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
								+ ChatColor.DARK_RED + "Unable to load dust (#0), dust name must contain %percent%");
						continue;
					}
					List<String> info = file.getStringList(path + ".Info");
					Language.t(info);
					boolean glint = file.getBoolean(path + ".Glint", true);
					int model = file.getInt(path + ".CustomModelData", 1);
					if(model <= 0) model = 1;
					List<String> allowed = file.getStringList(path + ".Allowed");
					List<Extractor> exs = new ArrayList<>(allowed.size());
					for(String allow : allowed) {
						Extractor ex = ExtractorRegistry.extractor(allow);
						if(ex != null) exs.add(ex);
					}
					int limit = file.getInt(path + ".Limit", -1);
					if(limit <= 0) limit = Integer.MAX_VALUE;
					boolean books = file.getBoolean(path + ".Books");
					List<String> list = new ArrayList<>();
					boolean recipe_toggle = file.getBoolean(path + ".Recipe.Toggle", true);
					Material[] recipe_matrix = new Material[9];
					list = file.getStringList(path + ".Recipe.Matrix");
					if(list.size() == 9) {
						int e = 0;
						for(int i = 0; i < 9; i++) if((recipe_matrix[i] = Utils.getMaterial(list.get(i), null)) == null) e++;
						if(e == 9) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
									+ ChatColor.RED + "Unable to load recipe for dust (" + key + "), recipe cannot be empty!");
						}
					} else if(list.isEmpty() == false) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] "
								+ ChatColor.RED + "Unable to load recipe for dust (" + key + "), recipe size can only be 9!");
					}
					int percent = file.getInt(path + ".Recipe.Percent");
					Dust dust = new Dust(key, material, name, info, glint, model, exs, books, limit,
							recipe_toggle, recipe_matrix, percent);
					dusts.add(dust);
				}
			}
		}
		return dusts;
	}
	
	public static void saveExtractor(ExtractorEdit e) {
		String path = "Extractors." + e.key;
		file.set(path + ".Material", e.material.name());
		file.set(path + ".Glint", e.glint);
		file.set(path + ".Chance.Toggle", e.chance_toggle);
	    file.set(path + ".Chance.Destroy", e.chance_destroy);
	    file.set(path + ".Cost.Toggle", e.cost_toggle);
	    file.set(path + ".Cost.Type", e.cost_type.name());
	    file.set(path + ".Cost.Material", e.cost_material.name());
	    file.set(path + ".Extraction", e.extraction.name());
	    file.set(path + ".Recipe.Toggle", e.recipe_toggle);
	    List<String> recipe = new ArrayList<>(9);
	    for(RecipeItem m : e.recipe_matrix) recipe.add(m == null ? "EMPTY"
	    		: m.material().name() + (m.amount() > 1 ? ":" + m.amount() : ""));
	    file.set(path + ".Recipe.Matrix", recipe);
	    save();
	}
	
	public static void save(String path, Object o) {
		file.set(path, o);
		save();
	}

	public static void save() {
		try {
			file.save(f);
		} catch (IOException e) {}
	}

}
