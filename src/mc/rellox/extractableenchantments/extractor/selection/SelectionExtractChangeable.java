package mc.rellox.extractableenchantments.extractor.selection;

import java.util.ArrayList;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.api.extractor.IExtractPrice;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.extractor.ISelectionExtractChangeable;
import mc.rellox.extractableenchantments.api.extractor.extract.ExtractType;
import mc.rellox.extractableenchantments.api.item.enchantment.IEnchantment;
import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;
import mc.rellox.extractableenchantments.configuration.Configuration.CF;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.configuration.Settings;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;
import mc.rellox.extractableenchantments.text.Text;

public class SelectionExtractChangeable implements ISelectionExtractChangeable, Listener {
	
	private final IExtractor extractor;
	private final Player player;
	
	private final ItemStack item_extractor;
	
	private ItemStack item_enchanted;
	
	private final List<ILevelledEnchantment> enchantments;
	
	private final Inventory v;
	
	public SelectionExtractChangeable(IExtractor extractor, Player player) {
		Bukkit.getPluginManager().registerEvents(this, ExtractableEnchantments.instance());
		
		this.extractor = extractor;
		this.player = player;
		
		this.item_extractor = extractor.item().item();
		
		this.enchantments = new ArrayList<>();
		
		int s = Settings.settings.extraction_selection_rows + 1;
		this.v = Bukkit.createInventory(player, s * 9,
				Language.get("Extraction.selection.name").text());
		update();
		
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
	public ItemStack item() {
		return item_enchanted;
	}
	
	@Override
	public void set(ItemStack item) {
		item_enchanted = item;
		update();
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
		
		Inventory clicked = event.getClickedInventory();
		if(v.equals(clicked) == true) {
			int slot = event.getSlot();
			if(slot == 5) {
				if(item_enchanted == null) return;
				
				item_enchanted = null;
				enchantments.clear();
				update();
				return;
			}
			
			int s = slot - 9;
			if(s < 0 || s >= enchantments.size()) return;
			ILevelledEnchantment levelled = enchantments.get(s);

			IExtractPrice price = extractor.price();
			if(price.enabled() == true) price.price().remove(player);

			ExtractorRegistry.extract(extractor, player, item_enchanted, item_extractor,
					levelled, ExtractType.SELECTION);
			
			read();
			update();
		} else if(player.getInventory().equals(clicked) == true) {
			ItemStack item = event.getCurrentItem();
			
			if(ItemRegistry.nulled(item) == true) return;

			List<ILevelledEnchantment> list = EnchantmentRegistry.enchantments(extractor, player, item);
			boolean single = list.size() == 1 && item.getType() == Material.ENCHANTED_BOOK;
			if(list.isEmpty() == true || single == true) return;
			
			item_enchanted = item;
			enchantments.clear();
			enchantments.addAll(list);
			update();
		}
	}
	
	private void read() {
		if(item_enchanted == null) return;
		
		List<ILevelledEnchantment> list = EnchantmentRegistry.enchantments(extractor, player, item_enchanted);
		boolean single = list.size() == 1 && item_enchanted.getType() == Material.ENCHANTED_BOOK;
		if(list.isEmpty() == true || single == true) {
			item_enchanted = null;
			enchantments.clear();
			return;
		}
		
		enchantments.clear();
		enchantments.addAll(list);
	}
	
	@EventHandler
	private final void onClose(InventoryCloseEvent event) {
		Inventory i = event.getInventory();
		if(v.equals(i) == false) return;
		unregister();
	}

	@EventHandler
	private final void onQuit(PlayerQuitEvent event) {
		if(event.getPlayer().equals(player) == false) return;
		unregister();
	}
	
	private void unregister() {
		HandlerList.unregisterAll(this);
	}
	
	private ItemStack item_book(ILevelledEnchantment levelled) {
		IEnchantment enchantment = levelled.enchantment();
		
		ItemStack item = Settings.settings.extraction_selection_item_book.generic();
		
		ItemMeta meta = item.getItemMeta();
		
		String color;
		if(enchantment.curse() == true) color = CF.l.color_curse;
		else if(enchantment.minecraft() == true) color = CF.l.color_minecraft;
		else color = CF.l.color_custom;
		
		meta.setDisplayName(Language.get("Extraction.selection.enchantment.name",
				"enchantment", color + enchantment.name()).text());
		
		meta.setLore(Text.toText(Language.list("Extraction.selection.enchantment.info")));
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack item_background() {
		return Settings.settings.extraction_selection_item_background.generic();
	}
	
}
