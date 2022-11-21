package mc.rellox.extractableenchantments.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.ExtractionType;
import mc.rellox.extractableenchantments.usage.CostType;
import mc.rellox.extractableenchantments.utils.Utils;

public class ExtractorEdit implements Listener {
	
	private final Player player;
	protected final String key;
	
	private final Inventory v;
	
	protected Material material;
	protected ExtractionType extraction;
	protected boolean glint;
	protected boolean chance_toggle;
	protected boolean chance_destroy;
	protected boolean cost_toggle;
	protected CostType cost_type;
	protected Material cost_material;
	protected boolean recipe_toggle;
	protected Material[] recipe_matrix;
	
	private final String name;
	private final List<String> info;
	private final int chance_min, chance_max, cost_value;
	
	private final RecipeEdit re;
	
	private boolean in;
	
	public ExtractorEdit(Player player, Extractor ex) {
		Bukkit.getPluginManager().registerEvents(this, ExtractableEnchantments.instance());
		this.player = player;
		this.key = ex.key;
		this.v = Bukkit.createInventory(null, 9 * 6, "Edit Extractor (" + key + ")");

		this.material = ex.material;
		this.extraction = ex.extraction;
		this.glint = ex.glint;
		this.chance_toggle = ex.chance_toggle;
		this.chance_destroy = ex.chance_destroy;
		this.cost_toggle = ex.cost_toggle;
		this.cost_type = ex.cost_type;
		this.cost_material = ex.cost_material;
		this.recipe_toggle = ex.recipe_toggle;
		this.recipe_matrix = ex.recipe_matrix;
		
		this.name = ex.name;
		this.info = ex.info;
		this.chance_min = ex.chance_min;
		this.chance_max = ex.chance_max;
		this.cost_value = ex.cost_value;
		
		this.re = new RecipeEdit();
		
		ItemStack x = x();
		for(int i = 0; i < v.getSize(); i++) v.setItem(i, x);
		v.setItem(38, cancel());
		v.setItem(42, save());
		update();
	}
	
	public void open() {
		re.in = false;
		in = true;
		player.openInventory(v);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
	}
	
	private void unregister() {
		HandlerList.unregisterAll(this);
	}
	
	private void update() {
		ItemStack x = x();
		
		v.setItem(4, info());
		
		v.setItem(10, material());
		v.setItem(11, extraction());
		v.setItem(12, glint());

		v.setItem(14, cost_toggle());
		v.setItem(23, cost_toggle ? cost_type() : x);
		v.setItem(32, cost_toggle ? cost_type == CostType.ECONOMY ? cost_material() : x : x);
		v.setItem(15, chance_toggle());
		v.setItem(24, chance_toggle ? chance_destroy() : x);
		v.setItem(16, recipe_toggle());
		v.setItem(25, recipe_toggle ? recipe_edit() : x);
	}
	
	@EventHandler
	private final void onClick(InventoryClickEvent event) {
		if(event.getWhoClicked().equals(player) == false) return;
		Inventory ci = event.getClickedInventory();
		if(ci == null) return;
		event.setCancelled(true);
		if(ci.equals(v) == true) {
			int s = event.getSlot();
			if(s == 38) {
				in = false;
				unregister();
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				player.closeInventory();
				player.sendMessage(ChatColor.DARK_AQUA + "[EE] " + ChatColor.RED + "Editing cancelled!");
				return;
			} else if(s == 42) {
				in = false;
				unregister();
				Configuration.saveExtractor(this);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
				player.closeInventory();
				player.sendMessage(ChatColor.DARK_AQUA + "[EE] " + ChatColor.GREEN + "New extractor values saved! "
						+ "Do " + ChatColor.YELLOW + ChatColor.ITALIC + "/ee reload " + ChatColor.GREEN + "to update!");
				return;
			} else if(s == 10) {
				ItemStack curs = event.getCursor();
				if(Utils.isNull(curs) == true) return;
				material = curs.getType();
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			} else if(s == 11) {
				extraction = extraction == ExtractionType.RANDOM ? ExtractionType.SELECTION : ExtractionType.RANDOM;
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			} else if(s == 12) {
				glint = !glint;
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			} else if(s == 14) {
				cost_toggle = !cost_toggle;
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			} else if(s == 15) {
				chance_toggle = !chance_toggle;
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			} else if(s == 16) {
				recipe_toggle = !recipe_toggle;
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			} else if(s == 23) {
				if(cost_toggle == false) return;
				cost_type = cost_type.next();
				if(cost_type == CostType.ECONOMY
						&& ExtractableEnchantments.ECONOMY.get() == null) cost_type = cost_type.next();
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			} else if(s == 24) {
				if(chance_toggle == false) return;
				chance_destroy = !chance_destroy;
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			} else if(s == 25) {
				if(recipe_toggle == false) return;
				new BukkitRunnable() {
					@Override
					public void run() {
						re.open();
					}
				}.runTaskLater(ExtractableEnchantments.instance(), 1);
			} else if(s == 32) {
				if(cost_toggle == false || cost_type != CostType.ECONOMY) return;
				ItemStack curs = event.getCursor();
				if(Utils.isNull(curs) == true || curs.getMaxStackSize() < 16) return;
				cost_material = curs.getType();
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2f, 2f);
			}
			update();
		} else if(ci.equals(re.v) == true) re.onClick(event);
		else event.setCancelled(false);
	}

	@EventHandler
	private final void onDrag(InventoryDragEvent event) {
		Inventory i = event.getInventory();
		if(i.equals(re.v) == false) return;
		re.updateMatrix();
	}
	
	@EventHandler
	private final void onQuit(PlayerQuitEvent event) {
		if(player.isOnline() == false) unregister();
	}
	
	@EventHandler
	private final void onClose(InventoryCloseEvent event) {
		Inventory i = event.getInventory();
		if(i == null) return;
		if(i.equals(v) == true) {
			if(in == false || re.in == true) return;
			unregister();
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			player.sendMessage(ChatColor.DARK_AQUA + "[EE] " + ChatColor.RED + "Editing cancelled!");
		} else if(i.equals(re.v) == true) {
			if(in == true || re.in == false) return;
			new BukkitRunnable() {
				@Override
				public void run() {
					open();
				}
			}.runTaskLater(ExtractableEnchantments.instance(), 1);
		}
	}

	private ItemStack info() {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Extractor Info " + ChatColor.DARK_AQUA + "(" + key + ")");
		meta.addItemFlags(ItemFlag.values());
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Material: " + ChatColor.YELLOW + Utils.displayName(material));
		lore.add("");
		lore.add(ChatColor.GRAY + "Name: " + ChatColor.RESET + name);
		lore.add(ChatColor.GRAY + "Info:");
		if(info.isEmpty() == true) lore.add(ChatColor.GRAY + "  EMPTY");
		else for(String s : info) lore.add("  " + s);
		lore.add("");
		lore.add(ChatColor.GRAY + "Glint: " + b(glint));
		lore.add("");
		lore.add(ChatColor.GRAY + "Chance Toggle: " + b(chance_toggle));
		lore.add(ChatColor.GRAY + "Chance Destroy: " + b(chance_destroy));
		lore.add(ChatColor.GRAY + "Chance Minimum: " + ChatColor.GOLD + chance_min);
		lore.add(ChatColor.GRAY + "Chance Maximum: " + ChatColor.GOLD + chance_max);
		lore.add("");
		lore.add(ChatColor.GRAY + "Cost Toggle: " + b(cost_toggle));
		lore.add(ChatColor.GRAY + "Cost Type: " + ChatColor.BLUE + cost_type.name());
		lore.add(ChatColor.GRAY + "Cost Material: " + ChatColor.BLUE + cost_material.name());
		lore.add(ChatColor.GRAY + "Cost Value: " + ChatColor.BLUE + cost_value);
		lore.add("");
		lore.add(ChatColor.GRAY + "Extraction: " + ChatColor.DARK_PURPLE + extraction.name());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack material() {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Material: " + ChatColor.RESET + Utils.displayName(material));
		meta.addItemFlags(ItemFlag.values());
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click with an item");
		lore.add(ChatColor.GRAY + "on your cursor to swap");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack extraction() {
		ItemStack item = new ItemStack(Material.HOPPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Extraction: " + ChatColor.AQUA + extraction.name());
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click to change extraction");
		lore.add(ChatColor.GRAY + "type to " + ChatColor.DARK_AQUA
				+ (extraction == ExtractionType.RANDOM ? ExtractionType.SELECTION : ExtractionType.RANDOM).name());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack glint() {
		ItemStack item = new ItemStack(Material.COAL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Glint: " + (glint ? ChatColor.GREEN : ChatColor.RED) + b(glint));
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click to toggle");
		lore.add(ChatColor.GRAY + "glint to " + ChatColor.DARK_AQUA + b(!glint));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack chance_toggle() {
		ItemStack item = new ItemStack(Material.EMERALD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Chance: " + (chance_toggle ? ChatColor.GREEN : ChatColor.RED) + b(chance_toggle));
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click to toggle");
		lore.add(ChatColor.GRAY + "chance to " + ChatColor.DARK_AQUA + b(!chance_toggle));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack chance_destroy() {
		ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Destroying: " + (chance_destroy ? ChatColor.GREEN : ChatColor.RED) + b(chance_destroy));
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click to toggle");
		lore.add(ChatColor.GRAY + "destroying to " + ChatColor.DARK_AQUA + b(!chance_destroy));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack cost_toggle() {
		ItemStack item = new ItemStack(Material.IRON_INGOT);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Cost: " + (cost_toggle ? ChatColor.GREEN : ChatColor.RED) + b(cost_toggle));
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click to toggle");
		lore.add(ChatColor.GRAY + "cost to " + ChatColor.DARK_AQUA + b(!cost_toggle));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack cost_type() {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click to change cost type");
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add("" + ChatColor.WHITE + ChatColor.ITALIC + "Cost Types:");
		lore.add(ct(CostType.EXPERIENCE_POINTS));
		lore.add(ct(CostType.EXPERIENCE_LEVELS));
		lore.add(ct(CostType.MATERIAL));
		boolean me = ExtractableEnchantments.ECONOMY.get() == null;
		lore.add((me ? ChatColor.STRIKETHROUGH : "")
				+ ct(CostType.ECONOMY) + (me ? ChatColor.GRAY + " (Missing Vault plugin)" : ""));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private String ct(CostType type) {
		return ChatColor.DARK_GRAY + "» " + (cost_type == type ? ChatColor.GREEN : ChatColor.RED) + type.name();
	}

	private ItemStack cost_material() {
		ItemStack item = new ItemStack(cost_material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Cost Material: " + ChatColor.RESET + Utils.displayName(cost_material));
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click with an item");
		lore.add(ChatColor.GRAY + "on your cursor to swap");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack recipe_toggle() {
		ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Recipe: " + (recipe_toggle ? ChatColor.GREEN : ChatColor.RED) + b(recipe_toggle));
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click to toggle");
		lore.add(ChatColor.GRAY + "crafting recipe to " + ChatColor.DARK_AQUA + b(!recipe_toggle));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack recipe_edit() {
		ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Edit Recipe");
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + "Click to edit recipe");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private class RecipeEdit {
		
		private final Inventory v;
		private boolean in;
		
		public RecipeEdit() {
			this.v = Bukkit.createInventory(null, 9 * 5, "Edit Recipe (" + key + ")");
			ItemStack x = x();
			for(int i = 0; i < v.getSize(); i++) v.setItem(i, x);
			v.setItem(24, back());
			update();
		}
		
		public void open() {
			ExtractorEdit.this.in = false;
			in = true;
			player.openInventory(v);
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 2f, 2f);
		}
		
		public void update() {
			for(int i = 0; i < 9; i++) v.setItem(10 + (i % 3) + (9 * (i / 3)), m(recipe_matrix[i]));
		}
		
		public void updateMatrix() {
			new BukkitRunnable() {
				@Override
				public void run() {
					int[] ss = {10, 11, 12, 19, 20, 21, 28, 29, 30};
					ItemStack slot;
					for(int i = 0; i < 9; i++) 
						recipe_matrix[i] = (slot = v.getItem(ss[i])) == null ? null : slot.getType();
					update();
				}
			}.runTaskLater(ExtractableEnchantments.instance(), 1);
		}
		
		private final void onClick(InventoryClickEvent event) {
			int s = event.getSlot();
			if(s == 24) {
				new BukkitRunnable() {
					@Override
					public void run() {
						ExtractorEdit.this.open();
					}
				}.runTaskLater(ExtractableEnchantments.instance(), 1);
			} else {
				int t = s - 10, i = t - (6 * (t / 9));
				if(i < 0 || i >= 9) return;
				event.setCancelled(false);
				updateMatrix();
			}
		}
		
	}
	
	private static String b(boolean b) {
		return b ? "True" : "False";
	}
	
	private static ItemStack save() {
		ItemStack item = new ItemStack(Material.LIME_CONCRETE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Save and Exit");
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack cancel() {
		ItemStack item = new ItemStack(Material.RED_CONCRETE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Cancel and Exit");
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack back() {
		ItemStack item = new ItemStack(Material.YELLOW_CONCRETE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Back");
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack m(Material m) {
		return m == null ? null : new ItemStack(m);
	}
	
	private static ItemStack x() {
		ItemStack item = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}

}
