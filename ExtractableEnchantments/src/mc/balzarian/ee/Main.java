package mc.balzarian.ee;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	public static Plugin plugin;
	private static File cf;
	public static FileConfiguration config;
	public static ShapedRecipe recipe;
	public static NamespacedKey key;
	public static Material material;
	public static String name;
	public static List<String> lore;
	public static boolean toggle;
	public static boolean glint;
	public static String message;
	public static RecipeEditor re;
	
	@Override
	public void onEnable() {
		Main.plugin = this;
		createConfig();
		List<String> lore = new ArrayList<>();
		lore.add("&8* &3Apply to an item to remove");
		lore.add("&3a random enchantment");
		config.addDefault("Recipe.0.0", Material.EXPERIENCE_BOTTLE.name());
		config.addDefault("Recipe.0.1", Material.COAL_BLOCK.name());
		config.addDefault("Recipe.0.2", Material.EXPERIENCE_BOTTLE.name());
		config.addDefault("Recipe.1.0", Material.COAL_BLOCK.name());
		config.addDefault("Recipe.1.1", Material.BOOK.name());
		config.addDefault("Recipe.1.2", Material.COAL_BLOCK.name());
		config.addDefault("Recipe.2.0", Material.EXPERIENCE_BOTTLE.name());
		config.addDefault("Recipe.2.1", Material.COAL_BLOCK.name());
		config.addDefault("Recipe.2.2", Material.EXPERIENCE_BOTTLE.name());
		config.addDefault("Material", Material.NETHER_BRICK.name());
		config.addDefault("Name", "&bEnchantments Extractor");
		config.addDefault("Lore", lore);
		config.addDefault("Toggle", true);
		config.addDefault("Glint", true);
		config.addDefault("Message", "Extracted");
		config.options().copyDefaults(true);
		saveC();
		Main.key = new NamespacedKey(Main.plugin, "extractableenchantments");
		Main.updateMaterial();
		Main.updateName();
		Main.updateLore();
		Main.updateToggle();
		Main.updateGlint();
		Main.updateMessage();
		Main.updateRecipe();
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Extractable Enchantments" + ChatColor.GREEN + " is enabled!");
		Bukkit.getPluginManager().registerEvents(this, this);
		Commands.command = Bukkit.getPluginCommand("extractableenchantments");
		if(Commands.command == null) Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Command " 
				+ ChatColor.AQUA + "/extractableenchantments" + ChatColor.RED + " was not found!");
		
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Extractable Enchantments" + ChatColor.GOLD + " is disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return Commands.onCommand(sender, command, label, args);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Commands.onTabComplete(sender, command, alias, args);
	}

	public void createConfig() {
		cf = new File(getDataFolder(), "config.yml");
		if(cf.getParentFile().exists() == false) cf.getParentFile().mkdirs();
		if(cf.exists() == true) config = YamlConfiguration.loadConfiguration(cf);
		try {
			cf.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}
		config = YamlConfiguration.loadConfiguration(cf);
	}

	public static void saveC() {
		try {
			config.save(cf);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setRecipe(Material[][] matrix) {
		for(int i = 0; i < 3; i++) for(int j = 0; j < 3; j++) config.set("Recipe." + i + "." + j, matrix[j][i].name());
		saveC();
		updateRecipe();
	}
	
	public static ShapedRecipe getRecipe() {
		Material[][] matrix = new Material[3][3];
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				String s = config.getString("Recipe." + i + "." + j);
				if(s == null) return null;
				try {
					matrix[i][j] = Material.valueOf(s);
				} catch(IllegalArgumentException x) {
					return null;
				}
			}
		}
		String[] shape = new String[3];
		for(int i = 0; i < 3; i++) shape[i] = (i * 3 + 1) + "" + (i * 3 + 2)  + "" + (i * 3 + 3);
		ShapedRecipe r = new ShapedRecipe(Main.key, Main.scroll());
		r.shape(shape);
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				Material m = matrix[i][j];
				if(m == Material.AIR) continue;
				r.setIngredient(shape[i].toCharArray()[j], m);
			}
		}
		return r;
	}
	
	public static void updateRecipe() {
		if(recipe != null) {
			Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
			while(it.hasNext() == true) {
				Recipe r = it.next();
				if(r instanceof ShapedRecipe == false) continue;
				ShapedRecipe sr = (ShapedRecipe) r;
				if(sr.getKey().equals(Main.key) == true) it.remove();
			}
		}
		Main.recipe = getRecipe();
		if(Main.recipe == null) return;
		Bukkit.addRecipe(Main.recipe);
	}
	
	@EventHandler
	public void onInteract(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory c = event.getClickedInventory();
		if(c == null) return;
		if(event.isLeftClick() == false && event.isRightClick() == false) return;
		ItemStack curr = event.getCurrentItem();
		ItemStack curs = event.getCursor();
		if(curs.isSimilar(Main.scroll()) == false) return;
		if(curr == null) return;
		if(curr.hasItemMeta() == false) return;
		ItemMeta meta = curr.getItemMeta();
		if(meta.hasEnchants() == false || meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) == true) return;
		if(curs.getAmount() > 1) {
			player.sendMessage(ChatColor.DARK_RED + "You must use only one " + name);
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		Map<Enchantment, Integer> map = meta.getEnchants();
		Enchantment ee = null;
		int l = 0;
		int r = new Random().nextInt(map.size());
		int i = 0;
		for(Enchantment e : map.keySet()) {
			if(i != r) {
				i++;
				continue;
			}
			ee = e;
			l = map.get(e);
			break;
		}
		if(ee == null) return;
		event.setCancelled(true);
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta bm = (EnchantmentStorageMeta) book.getItemMeta();
		bm.addStoredEnchant(ee, l, true);
		book.setItemMeta(bm);
		meta.removeEnchant(ee);
		curr.setItemMeta(meta);
		player.setItemOnCursor(book);
		ChatColor col = ChatColor.YELLOW;
		if(ee.equals(Enchantment.BINDING_CURSE) || ee.equals(Enchantment.VANISHING_CURSE)) col = ChatColor.RED;
		String s = ChatColor.DARK_AQUA + "Extracted: " + col+ Main.getEnchantmentName(ee);
		if(ee.getMaxLevel() > 1) s += " " + Main.getRoman(l);
		player.sendMessage(s);
		player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2f, 0f);
		player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 0f);
		DustOptions o = new DustOptions(Color.BLACK, 1f);
		player.spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1, 0), 50, 0.2, 0.5, 0.2, 0, o);
 	}
	
	@EventHandler
	public void onCraftPrep(PrepareItemCraftEvent event) {
		ItemStack[] matrix = event.getInventory().getMatrix();
		for(int i = 0; i < matrix.length; i++) {
			ItemStack item = matrix[i];
			if(item == null) continue;
			if(item.isSimilar(Main.scroll()) == false) continue;
			event.getInventory().setResult(null);
			return;
		}
	}
	
	public static ItemStack scroll(int a) {
		ItemStack item = new ItemStack(Main.material, a);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Main.name);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		if(Main.glint == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		if(Main.toggle == true) {
			List<String> lore = new ArrayList<>();
			lore.add("");
			for(String l : Main.lore) {
				String line = ChatColor.translateAlternateColorCodes('&', l);
				lore.add(ChatColor.RESET + line);
			}
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack scroll() {
		return scroll(1);
	}
	
	public static String getRoman(int l) {
		if(l == 1) return "I";
		if(l == 2) return "II";
		if(l == 3) return "III";
		if(l == 4) return "IV";
		if(l == 5) return "V";
		return "";
	}
	
	public static void updateName() {
		String s = config.getString("Name");
		if(s.isEmpty() == true) Main.name = ChatColor.AQUA + "Enchantment Extracort";
		else Main.name = ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static void updateLore() {
		List<String> lore = config.getStringList("Lore");
		boolean n = false;
		if(lore == null) n = true;
		else if(lore.isEmpty() == true) n = true;
		if(n == true) {
			Main.lore = new ArrayList<>();
			Main.lore.add(ChatColor.DARK_GRAY + "* " + ChatColor.DARK_AQUA + "Apply to an item to remove");
			Main.lore.add(ChatColor.DARK_AQUA + "a random enchantment");
		} else {
			Main.lore = new ArrayList<>();
			for(String s : lore) Main.lore.add(ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	
	public static void updateToggle() {
		Main.toggle = config.getBoolean("Toggle");
	}
	
	public static void updateMaterial() {
		String s = config.getString("Material");
		if(s.isEmpty() == true) Main.material = Material.NETHER_BRICK;
		else {
			try {
				Material m = Material.valueOf(s);
				Main.material = m;
			} catch (IllegalArgumentException e) {
				Main.material = Material.NETHER_BRICK;
			}
		}
	}
	
	public static void updateGlint() {
		Main.glint = config.getBoolean("Glint");
	}
	
	public static void updateMessage() {
		String s = config.getString("Message");
		if(s.isEmpty() == true) Main.message = ChatColor.DARK_AQUA + "Extracted";
		else Main.message = ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static String getEnchantmentName(Enchantment e) {
		if(e.equals(Enchantment.ARROW_DAMAGE)) return "Power";
		if(e.equals(Enchantment.ARROW_FIRE)) return "Flame";
		if(e.equals(Enchantment.ARROW_INFINITE)) return "Infinity";
		if(e.equals(Enchantment.ARROW_KNOCKBACK)) return "Punch";
		if(e.equals(Enchantment.BINDING_CURSE)) return "Curse of Binding";
		if(e.equals(Enchantment.CHANNELING)) return "Channeling";
		if(e.equals(Enchantment.DAMAGE_ALL)) return "Sharpness";
		if(e.equals(Enchantment.DAMAGE_ARTHROPODS)) return "Bane of Arthropods";
		if(e.equals(Enchantment.DAMAGE_UNDEAD)) return "Smite";
		if(e.equals(Enchantment.DEPTH_STRIDER)) return "Depth Strider";
		if(e.equals(Enchantment.DIG_SPEED)) return "Efficiency";
		if(e.equals(Enchantment.DURABILITY)) return "Unbreaking";
		if(e.equals(Enchantment.FIRE_ASPECT)) return "Fire Aspect";
		if(e.equals(Enchantment.FROST_WALKER)) return "Frost Walker";
		if(e.equals(Enchantment.IMPALING)) return "Impaling";
		if(e.equals(Enchantment.KNOCKBACK)) return "Knockback";
		if(e.equals(Enchantment.LOOT_BONUS_BLOCKS)) return "Fortune";
		if(e.equals(Enchantment.LOOT_BONUS_MOBS)) return "Looting";
		if(e.equals(Enchantment.LOYALTY)) return "Loyalty";
		if(e.equals(Enchantment.LUCK)) return "Luck of the Sea";
		if(e.equals(Enchantment.LURE)) return "Lure";
		if(e.equals(Enchantment.MENDING)) return "Mending";
		if(e.equals(Enchantment.MULTISHOT)) return "Multishot";
		if(e.equals(Enchantment.OXYGEN)) return "Respiration";
		if(e.equals(Enchantment.PIERCING)) return "Piercing";
		if(e.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) return "Protection";
		if(e.equals(Enchantment.PROTECTION_EXPLOSIONS)) return "Blast Protection";
		if(e.equals(Enchantment.PROTECTION_FALL)) return "Feather Falling";
		if(e.equals(Enchantment.PROTECTION_FIRE)) return "Fire Protection";
		if(e.equals(Enchantment.PROTECTION_PROJECTILE)) return "Projectile Protection";
		if(e.equals(Enchantment.QUICK_CHARGE)) return "Quick Charge";
		if(e.equals(Enchantment.RIPTIDE)) return "Riptide";
		if(e.equals(Enchantment.SILK_TOUCH)) return "Silk Touch";
		if(e.equals(Enchantment.SWEEPING_EDGE)) return "Sweeping Edge";
		if(e.equals(Enchantment.THORNS)) return "Thorns";
		if(e.equals(Enchantment.VANISHING_CURSE)) return "Curse of Vanishing";
		if(e.equals(Enchantment.WATER_WORKER)) return "Aqua Affinity";
		return "Unknown";
	}
	
	public static boolean isInteger(String number) {
		try {
			Integer.valueOf(number);
			return true;
		} catch(NumberFormatException ex) {
			return false;
		}
	}

	public static boolean isNull(ItemStack item) {
		if(item == null) return true;
		if(item.getType() == Material.AIR) return true;
		return false;
	}
}
