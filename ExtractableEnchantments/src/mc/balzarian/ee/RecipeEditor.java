package mc.balzarian.ee;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class RecipeEditor implements Listener {
	
	private Player player;
	private int[] matrix;
	private Inventory v;
	
	public RecipeEditor(Player player) {
		this.player = player;
		this.matrix = new int[] {3, 4, 5, 12, 13, 14, 21, 22, 23};
		setInv();
		this.player.openInventory(v);
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 2f, 1f);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void close() {
		HandlerList.unregisterAll(this);
		for(HumanEntity he : v.getViewers()) {
			he.closeInventory();
			break;
		}
		Main.re = null;
	}

	@EventHandler
	private void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(this.player.equals(player) == false) return;
		close();
	}

	@EventHandler
	private void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if(this.player.equals(player) == false) return;
		player.sendTitle("", ChatColor.RED + "Editing Cancelled", 5, 20, 5);
		close();
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(this.player.equals(player) == false) return;
		Inventory c = event.getClickedInventory();
		if(c == null) return;
		if(c.equals(v) == false) return;
		int s = event.getSlot();
		for(int i : matrix) if(s == i) return;
		event.setCancelled(true);
		if(s == 37) {
			player.sendTitle("", ChatColor.RED + "Editing Cancelled", 5, 20, 5);
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			close();
		} else if(s == 39) {
			clearMatrix();
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 2f, 0f);
		} else if(s == 41) {
			setMatrix();
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 2f, 1f);
		} else if(s == 43) {
			Material[][] matrix = new Material[3][3];
			int e = 0;
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 3; j++) {
					ItemStack item = v.getItem(i + 3 + j * 9);
					if(item == null) {
						matrix[i][j] = Material.AIR;
						e++;
					} else matrix[i][j] = item.getType();
				}
			}
			if(e >= 9) {
				player.sendTitle("", ChatColor.DARK_RED + "Cannot save an empty recipe", 5, 20, 5);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				close();
			} else {
				player.sendTitle("", ChatColor.GREEN + "Recipe Saved", 5, 20, 5);
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2f, 2f);
				Main.setRecipe(matrix);
				close();
			}
		}
	}
	
	private void setInv() {
		v = Bukkit.createInventory(null, 54, ChatColor.BLACK + "* Extractor Recipe Editor");
		for(int i = 0; i < v.getSize(); i++) v.setItem(i, x());
		setMatrix();
		v.setItem(37, c());
		v.setItem(39, l());
		v.setItem(41, r());
		v.setItem(43, s());
	}
	
	private void setMatrix() {
		ShapedRecipe r = Main.recipe;
		if(r == null) {
			clearMatrix();
			return;
		}
		String[] shape = r.getShape();
		Map<Character, ItemStack> im = r.getIngredientMap();
		for(int i = 0; i < 3; i++) for(int j = 0; j < 3; j++) v.setItem(i + 3 + j * 9, im.get(shape[j].toCharArray()[i]));
	}
	
	private void clearMatrix() {
		for(int i = 0; i < 3; i++) for(int j = 0; j < 3; j++) v.setItem(i + 3 + j * 9, null);
	}

	private ItemStack c() {
		ItemStack item = new ItemStack(Material.RED_CONCRETE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Cancel Editing");
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack l() {
		ItemStack item = new ItemStack(Material.ORANGE_CONCRETE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Clear Recipe");
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack r() {
		ItemStack item = new ItemStack(Material.BLUE_CONCRETE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE + "Reset Recipe");
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack s() {
		ItemStack item = new ItemStack(Material.GREEN_CONCRETE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Save Recipe");
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack x() {
		ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}

}
