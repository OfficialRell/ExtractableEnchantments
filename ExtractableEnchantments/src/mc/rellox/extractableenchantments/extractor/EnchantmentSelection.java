package mc.rellox.extractableenchantments.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.Extract;
import mc.rellox.extractableenchantments.utils.Utils;

public class EnchantmentSelection implements Listener {

	private final Extractor extractor;
	private final Player player;
	private final ItemStack item_extractor, item_used;
	
	private final Map<Enchantment, Integer> map;
	private final Enchantment[] es;
	
	private final Inventory v;
	
	public EnchantmentSelection(Extractor extractor, Player player, ItemStack item_extractor, ItemStack item_used) {
		Bukkit.getPluginManager().registerEvents(this, ExtractableEnchantments.instance());
		this.extractor = extractor;
		this.player = player;
		this.item_extractor = item_extractor;
		this.item_used = item_used;
		
		ItemMeta meta = item_used.getItemMeta();
		Map<Enchantment, Integer> map = meta instanceof EnchantmentStorageMeta storage ?
				storage.getStoredEnchants() : meta.getEnchants();
		this.map = map.entrySet().stream()
				.filter(e ->  extractor.filter().test(e.getKey()))
			.collect(Collectors.toMap(t -> t.getKey(), t -> t.getValue()));
		
		this.es = this.map.keySet().toArray(Enchantment[]::new);
		
		int z = es.length, s = (z + 8) / 9 + 1;
		if(s <= 0) s = 1;
		else if(s > 6) s = 6;
		this.v = Bukkit.createInventory(player, s * 9, Language.extractor_selection_name());
		update();
		player.setItemOnCursor(null);
		player.closeInventory();
		player.openInventory(v);
	}
	
	private void unregister() {
		HandlerList.unregisterAll(this);
	}
	
	private void update() {
		ItemStack x = x();
		int i, j = Math.min(es.length, v.getSize() - 9);
		for(i = 0; i < v.getSize(); i++) v.setItem(i, x);
		i = 0;
		for(; i < j; i++) v.setItem(i + 9, book(es[i], map.get(es[i])));
		v.setItem(3, item_extractor);
		v.setItem(5, item_used);
	}
	
	@EventHandler
	private final void onClick(InventoryClickEvent event) {
		Inventory i = event.getInventory();
		if(v.equals(i) == false) return;
		event.setCancelled(true);
		if((i = event.getClickedInventory()) == null) return;
		if(v.equals(i) == false) return;
		int s = event.getSlot() - 9;
		if(s < 0 || s >= es.length) return;
		Enchantment removed = es[s];
		if(removed == null) return;
		int level = map.get(removed);
		if(extractor.extract_unsafe == false && level > removed.getMaxLevel()) {
			player.sendMessage(Language.extraction_unsafe());
			return;
		}
		unregister();
		player.closeInventory();
		if(extractor.cost_toggle == true) extractor.cost().remove(player);
		ExtractorRegistry.extract(extractor, player, item_used, removed, level,
				!extractor.chance(item_extractor), item_used.getItemMeta() instanceof EnchantmentStorageMeta);
	}
	
	@EventHandler
	private final void onClose(InventoryCloseEvent event) {
		Inventory i = event.getInventory();
		if(i == null || i.equals(v) == false) return;
		unregister();
		if(player.isOnline() == true) {
			if(Utils.slots(player) <= 0) player.getWorld().dropItem(player.getLocation(), item_extractor);
			else player.getInventory().addItem(item_extractor);
		} else player.getWorld().dropItem(player.getLocation(), item_extractor);
	}

	@EventHandler
	private final void onQuit(PlayerQuitEvent event) {
		if(event.getPlayer().equals(player) == false) return;
		unregister();
		player.getWorld().dropItem(player.getLocation(), item_extractor);
	}
	
	private static ItemStack book(Enchantment e, int l) {
		ItemStack item = new ItemStack(Material.BOOK);
		ItemMeta meta = item.getItemMeta();
		boolean b = Extract.MINECRAFT.filter().test(e);
		meta.setDisplayName(b ? Language.extractor_selection_name(e, l) : Language.extractor_selection_name_custom(e, l));
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		meta.addItemFlags(ItemFlag.values());
		List<String> lore = new ArrayList<>();
		lore.add(Language.extractor_selection_info());	
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack x() {
		ItemStack item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}

}
