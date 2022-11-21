package mc.rellox.extractableenchantments.dust;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.configuration.Configuration;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.utils.Utils;

public final class DustRegistry implements Listener {
	
	public static NamespacedKey key_dust, key_percent, key_chance;
	
	public static final List<Dust> DUSTS = new LinkedList<>();

	public static void initialize() {
		Bukkit.getPluginManager().registerEvents(new DustRegistry(), ExtractableEnchantments.instance());
		key_dust = new NamespacedKey(ExtractableEnchantments.instance(), "dust");
		key_percent = new NamespacedKey(ExtractableEnchantments.instance(), "percent");
		key_chance = new NamespacedKey(ExtractableEnchantments.instance(), "chance");
		update();
	}

	public static void update() {
		DUSTS.clear();
		DUSTS.addAll(Configuration.dusts());
	}
	
	public static Dust dust(String key) {
		return DUSTS.stream()
				.filter(e -> e.key.equalsIgnoreCase(key))
				.findFirst()
				.orElse(null);
	}
	
	public static Dust dust(ItemStack item) {
		return DUSTS.stream()
				.filter(e -> e.is(item))
				.findFirst()
				.orElse(null);
	}
	
	public static int readPercent(ItemStack item) {
		if(Utils.isNull(item) == true) return -1;
		PersistentDataContainer cont = item.getItemMeta().getPersistentDataContainer();
		return cont.getOrDefault(key_percent, PersistentDataType.INTEGER, -1);
	}
	
	public static void writePercent(ItemStack item, int percent) {
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer cont = meta.getPersistentDataContainer();
		cont.set(key_percent, PersistentDataType.INTEGER, percent);
		item.setItemMeta(meta);
	}
	
	public static int readChance(ItemStack item) {
		if(Utils.isNull(item) == true) return -1;
		PersistentDataContainer cont = item.getItemMeta().getPersistentDataContainer();
		return cont.getOrDefault(key_chance, PersistentDataType.INTEGER, -1);
	}
	
	public static void writeChance(ItemStack item, int chance) {
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer cont = meta.getPersistentDataContainer();
		cont.set(key_chance, PersistentDataType.INTEGER, chance);
		item.setItemMeta(meta);
	}
	
	private static boolean allow(Player player) {
		if(player.getGameMode() == GameMode.CREATIVE) {
			player.sendMessage(ChatColor.DARK_RED + "Cannot use dust while in creative mode, "
					+ "due to item duplication!");
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return false;
		}
		return true;
	}

	@EventHandler()
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.discoverRecipes(DUSTS.stream().filter(d -> d.recipe != null)
				.map(d -> d.recipe.getKey()).collect(Collectors.toList()));
	}

	@EventHandler(ignoreCancelled = true)
	private void onApply(InventoryClickEvent event) {
		if(event.getClickedInventory() == null) return;
		ItemStack curr = event.getCurrentItem(), curs = event.getCursor();
		Player player = (Player) event.getWhoClicked();
		if(Utils.isNull(curr) == false)  {
			if(Utils.isNull(curs) == true) {
				if(event.getClick() != ClickType.MIDDLE) return;
				ItemStack item_dust = curr;
				Dust dust = dust(item_dust);
				if(dust == null || allow(player) == false) return;
				event.setCancelled(true);
				if(player.hasPermission("ee.dust.split." + dust.key) == false) {
					player.sendMessage(Language.permission_warn_split());
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
				int perc_dust = readPercent(item_dust);
				if(perc_dust <= 1) return;
				int half = ((perc_dust - 1) >> 1) + 1;
				perc_dust -= half;
				writePercent(item_dust, perc_dust);
				dust.update(item_dust);
				ItemStack item_hand = dust.item(half);
				player.setItemOnCursor(item_hand);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_WOOL_BREAK, 2f, 1.5f);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 2f);
				return;
			}
			Dust dust = dust(curs);
			if(dust == null || allow(player) == false) return;
			Dust other = dust(curr);
			if(other != null) {
				if(dust.equals(other) == false) return;
				event.setCancelled(true);
				if(player.hasPermission("ee.dust.use." + dust.key) == false) {
					player.sendMessage(Language.permission_warn_dust());
					player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
					return;
				}
				ItemStack item_dust = curs, item_oth = curr;
				int perc_dust = readPercent(item_dust);
				if(perc_dust < 0) return;
				int perc_oth = readPercent(item_oth);
				if(perc_dust < 0) return;
				int can = other.limit - perc_oth;
				if(can <= 0) return;
				if(perc_dust > can) {
					perc_oth = other.limit;
					perc_dust -= can;
					writePercent(item_dust, perc_dust);
					dust.update(item_dust);
					writePercent(item_oth, perc_oth);
					other.update(item_oth);
				} else {
					perc_oth += perc_dust;
					writePercent(item_oth, perc_oth);
					other.update(item_oth);
					player.setItemOnCursor(null);
				}
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 1.5f);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
				return;
			}
			ItemStack item_dust = curs, item_hand = curr;
			byte b = 0;
			Extractor ex = ExtractorRegistry.extractor(item_hand);
			if(ex != null) b |= (dust.allow(ex) == true ? 1 : 0);
			else if(dust.books == true) b |= (item_hand.getType() == Material.ENCHANTED_BOOK ? 2 : 0);
			if(b == 0) return;
			if(player.hasPermission("ee.dust.use." + dust.key) == false) {
				player.sendMessage(Language.permission_warn_dust());
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
			int perc_hand = readChance(item_hand);
			if(perc_hand < 0) return;
			event.setCancelled(true);
			if(perc_hand >= 100) {
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
			int perc_dust = readPercent(item_dust);
			int r = 100 - perc_hand;
			if(perc_dust > r) {
				perc_hand = 100;
				perc_dust -= r;
				writeChance(item_hand, perc_hand);
				writePercent(item_dust, perc_dust);
				dust.update(item_dust);
			} else {
				perc_hand += perc_dust;
				writeChance(item_hand, perc_hand);
				player.setItemOnCursor(null);
			}
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
			player.playSound(player.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.75f, 2f);
			if(b == 1) Language.chance_extractor_set(item_hand, perc_hand);
			else Language.chance_book_set(item_hand, perc_hand);
		} else if(Utils.isNull(curs) == false) {
			if(event.getClick() != ClickType.MIDDLE) return;
			ItemStack item_dust = curs;
			Dust dust = dust(item_dust);
			if(dust == null || allow(player) == false) return;
			event.setCancelled(true);
			if(player.hasPermission("ee.dust.split." + dust.key) == false) {
				player.sendMessage(Language.permission_warn_split());
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
			int perc_dust = readPercent(item_dust);
			if(perc_dust <= 1) return;
			int half = ((perc_dust - 1) >> 1) + 1;
			perc_dust -= half;
			writePercent(item_dust, perc_dust);
			dust.update(item_dust);
			ItemStack item_current = dust.item(half);
			event.setCurrentItem(item_current);
			player.playSound(player.getEyeLocation(), Sound.BLOCK_WOOL_BREAK, 2f, 1.5f);
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 2f);
			return;
		}
	}
	
	public static boolean contains(ItemStack[] matrix) {
		for(ItemStack item : matrix) {
			for(Dust d : DUSTS) {
				if(d.is(item) == true) {
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	private void onCraftPrep(PrepareItemCraftEvent event) {
		Recipe r = event.getRecipe();
		if(r instanceof ShapedRecipe) {
			Dust du = null;
			for(Dust d : DUSTS) {
				if(d.recipe_toggle == false) continue;
				if(d.recipe != null && d.recipe.getKey().equals(((ShapedRecipe) r).getKey()) == true) {
					du = d;
					break;
				}
			}
			if(du == null) return;
			event.getInventory().setResult(du.item_static());
		}
	}

	@EventHandler
	private void onCraft(CraftItemEvent event) {
		Recipe r = event.getRecipe();
		if(r instanceof ShapedRecipe == false) return; 
		Dust du = null;
		for(Dust d : DUSTS) {
			if(d.recipe_toggle == false) continue;
			if(d.recipe != null && d.recipe.getKey().equals(((ShapedRecipe) r).getKey()) == true) {
				du = d;
				break;
			}
		}
		if(du == null) return;
		event.setCancelled(true);
		CraftingInventory ci = event.getInventory();
		ItemStack[] matrix = ci.getMatrix();
		if(contains(matrix) == true || ExtractorRegistry.contains(matrix) == true) {
			event.getInventory().setResult(null);
			return;
		}
		Player player = (Player) event.getWhoClicked();
		if(player.hasPermission("ee.dust.craft." + du.key) == false) {
			player.sendMessage(Language.permission_warn_craft());
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			event.getInventory().setResult(null);
			return;
		}
		int a = Stream.of(matrix)
			.filter(i -> i != null)
			.mapToInt(ItemStack::getAmount)
			.min().orElse(64);
		if(event.isShiftClick() == false) {
			if(event.getClick() == ClickType.NUMBER_KEY) {
				int button = event.getHotbarButton();
				PlayerInventory pi = player.getInventory();
				if(Utils.isNull(pi.getItem(button)) == false) return; 
				pi.setItem(button, du.item(du.percent));
				matrix(matrix, 1);
			} else {
				ItemStack curs = player.getItemOnCursor();
				if(Utils.isNull(curs) == false) return; 
				player.setItemOnCursor(du.item(du.percent));
				matrix(matrix, 1);
			}
		} else {
			int f = Utils.slots(player);
			if(f <= 0) return; 
			if(f >= a) {
				player.getInventory().addItem(du.items(du.percent, a));
				matrix(matrix, a);
			} else {
				player.getInventory().addItem(du.items(du.percent, f));
				matrix(matrix, f);
			}
		}
		ci.setMatrix(matrix);
	}

	private static void matrix(ItemStack[] matrix, int a) {
		for(int i = 0, l; i < matrix.length; i++) {
			ItemStack item = matrix[i];
			if(item != null) {
				if((l = item.getAmount() - a) <= 0) matrix[i] = null;
				else item.setAmount(l);
			}
		}
	}
}
