package mc.rellox.extractableenchantments.extractor;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.extractor.ISelectionExtract;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;
import mc.rellox.extractableenchantments.configuration.Configuration.CF;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.text.Text;

public class SelectionExtract implements ISelectionExtract, Listener {
	
	private final IExtractor extractor;
	private final Player player;
	
	private final ItemStack item_extractor, item_enchanted;
	
	private final List<ILevelledEnchantment> enchantments;
	
	private final Inventory v;
	
	public SelectionExtract(IExtractor extractor, Player player,
			ItemStack item_extractor, ItemStack item_enchanted,
			List<ILevelledEnchantment> enchantments) {
		Bukkit.getPluginManager().registerEvents(this, ExtractableEnchantments.instance());
		
		this.extractor = extractor;
		this.player = player;
		
		this.item_extractor = item_extractor;
		this.item_enchanted = item_enchanted;
		
		this.enchantments = enchantments;
		
		int size = (enchantments.size() + 8) / 9 + 1;
		if(size <= 0) size = 1;
		else if(size > 6) size = 6;
		this.v = Bukkit.createInventory(player, size * 9,
				Language.get("Extraction.selection.name").text());
		update();
		
		player.setItemOnCursor(null);
		player.closeInventory();
		player.openInventory(v);
	}

	@Override
	public Inventory inventory() {
		return v;
	}

	@Override
	public IExtractor extractor() {
		return extractor;
	}

	@Override
	public List<ILevelledEnchantment> enchantments() {
		return enchantments;
	}
	
	@Override
	public void update() {
		ItemStack x = item_background();
		for(int i = 0; i < v.getSize(); i++)
			v.setItem(i, x);
		
		int j = Math.min(enchantments.size(), v.getSize() - 9);
		for(int i = 0; i < j; i++)
			v.setItem(i + 9, item_book(enchantments.get(i)));
		
		v.setItem(3, item_extractor);
		v.setItem(5, item_enchanted);
	}
	
	@EventHandler
	private final void onClick(InventoryClickEvent event) {
		if(v.equals(event.getInventory()) == false) return;
		event.setCancelled(true);
		if(v.equals(event.getClickedInventory()) == false) return;
		
		int s = event.getSlot() - 9;
		if(s < 0 || s >= enchantments.size()) return;
		ILevelledEnchantment levelled = enchantments.get(s);
		
		unregister();
		player.closeInventory();
		
		if(extractor.price().enabled() == true) extractor.price().price().remove(player);
		
		ExtractorRegistry.extract(extractor, player, item_enchanted, levelled,
				extractor.chance().chance(item_extractor) == false);
	}
	
	@EventHandler
	private final void onClose(InventoryCloseEvent event) {
		Inventory i = event.getInventory();
		if(v.equals(i) == false) return;
		unregister();
		
		if(player.isOnline() == true) {
			if(ItemRegistry.free(player) <= 0)
				player.getWorld().dropItem(player.getLocation(), item_extractor);
			else player.getInventory().addItem(item_extractor);
		} else player.getWorld().dropItem(player.getLocation(), item_extractor);
	}

	@EventHandler
	private final void onQuit(PlayerQuitEvent event) {
		if(event.getPlayer().equals(player) == false) return;
		unregister();
		player.getWorld().dropItem(player.getLocation(), item_extractor);
	}
	
	private void unregister() {
		HandlerList.unregisterAll(this);
	}
	
	private ItemStack item_book(ILevelledEnchantment levelled) {
		IEnchantment enchantment = levelled.enchantment();
		
		ItemStack item = new ItemStack(Material.BOOK);
		ItemMeta meta = item.getItemMeta();
		
		String color;
		if(enchantment.curse() == true) color =CF.l.color_curse;
		else if(enchantment.minecraft() == true) color = CF.l.color_minecraft;
		else color = CF.l.color_custom;
		
		meta.setDisplayName(Language.get("Extraction.selection.enchantment.name",
				"enchantment", color + enchantment.name()).text());
		
		meta.setLore(Text.toText(Language.list("Extraction.selection.enchantment.info")));
		
		meta.addItemFlags(ItemFlag.values());
		ItemRegistry.glint(meta);
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack item_background() {
		ItemStack item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		return item;
	}
	
}
