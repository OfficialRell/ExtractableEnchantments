package mc.rellox.extractableenchantments.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import mc.rellox.extractableenchantments.api.extractor.IExtractPrice;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.extractor.IExtractorChance;
import mc.rellox.extractableenchantments.api.item.IExtractorItem;
import mc.rellox.extractableenchantments.api.item.order.IOrder;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.configuration.Settings;
import mc.rellox.extractableenchantments.text.content.Content;
import mc.rellox.extractableenchantments.utility.Keys;
import mc.rellox.extractableenchantments.utility.Utility;

public class ExtractorItem extends Item implements IExtractorItem {
	
	public IExtractor extractor;

	public ExtractorItem(Material material, List<Content> name, List<Content> info, boolean glint,
			int model, String tooltip) {
		super(material, name, info, glint, model, tooltip);
	}

	@Override
	public ItemStack constant() {
		ItemStack item = generic();
		ItemMeta meta = item.getItemMeta();
		
		List<Content> name = new ArrayList<>(this.name);
		if(name.size() > 0) meta.setDisplayName(name.remove(0).text());
		
		IOrder order = Settings.settings.order_extractor.oderer();
		
		order.named(name);
		
		order.submit("INFO", this::info);
		
		IExtractorChance chance = extractor.chance();
		if(chance.enabled() == true) {
			order.submit("CHANCE", () -> Language.list("Extractor.info.chance",
					"chance", Language.get("Extractor.info.unknown-chance")));
			if(chance.destroy() == true)
				order.submit("DESTROY", () -> Language.list("Extractor.info.destroy"));
		}
		
		IExtractPrice price = extractor.price();
		if(price.enabled() == true)
			order.submit("PRICE", () -> Language.list("Extractor.info.price",
					"price", price.price().text()));
		
		meta.setLore(order.build());
		
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public ItemStack item() {
		ItemStack item = generic();
		ItemMeta meta = item.getItemMeta();
		
		List<Content> name = new ArrayList<>(this.name);
		if(name.size() > 0) meta.setDisplayName(name.remove(0).text());
		
		IOrder order = Settings.settings.order_extractor.oderer();
		
		order.named(name);
		
		order.submit("INFO", this::info);
		
		int chance_value;
		IExtractorChance chance = extractor.chance();
		if(chance.enabled() == true) {
			chance_value = chance.roll();
			order.submit("CHANCE", () -> {
				List<Content> list = Language.list("Extractor.info.chance", "chance", chance_value);
				list.replaceAll(c -> Content.of(Content.of(Language.prefix_chance), c));
				return list;
			});
			if(chance.destroy() == true)
				order.submit("DESTROY", () -> Language.list("Extractor.info.destroy"));
		} else chance_value = 100;
		
		IExtractPrice price = extractor.price();
		if(price.enabled() == true)
			order.submit("PRICE", () -> Language.list("Extractor.info.price",
					"price", price.price().text()));
		
		meta.setLore(order.build());
		
		PersistentDataContainer data = meta.getPersistentDataContainer();
		data.set(Keys.extractor(), PersistentDataType.STRING, extractor.key());
		data.set(Keys.chance(), PersistentDataType.INTEGER, chance_value);
		
		if(extractor.stackable() == false || chance.enabled() == true)
			data.set(Keys.random(), PersistentDataType.INTEGER, Utility.random());
		
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public boolean match(ItemStack item) {
		if(item == null || item.hasItemMeta() == false) return false;
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();
		String saved = data.get(Keys.extractor(), PersistentDataType.STRING);
		return extractor.key().equalsIgnoreCase(saved) == true;
	}

}
