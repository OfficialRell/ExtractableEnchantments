package mc.rellox.extractableenchantments.dust;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.configuration.Configuration;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.utils.Utils;

public final class Dust {
	
	public final String key;
	
	private final Material material;
	private final String name;
	private final List<String> info;
	
	private final int model;
	private final boolean glint;
	
	private final List<Extractor> allowed;
	public final boolean books;
	
	public final int limit;
	
	public final boolean recipe_toggle;
	public final Material[] recipe_matrix;
	public final int percent;
	public final ShapedRecipe recipe;
	
	public Dust(String key, Material material, String name, List<String> info,
			boolean glint, int model, List<Extractor> allowed, boolean books, int limit,
			boolean recipe_toggle, Material[] recipe_matrix, int percent) {
		this.key = key;
		
		this.material = material;
		this.name = name;
		this.info = info;
		
		this.glint = glint;
		this.model = model;
		
		this.allowed = allowed;
		this.books = books;
		
		this.limit = limit;

		this.recipe_toggle = recipe_toggle;
		this.recipe_matrix = recipe_matrix;
		this.percent = percent;
		this.recipe = loadRecipe();
	}
	
	private ShapedRecipe loadRecipe() {
		if(recipe_toggle == false) return null;
		int e = 9 - (int) Stream.of(recipe_matrix)
				.filter(x -> x != null).count();
		if(e == 9) return null;
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(ExtractableEnchantments.instance(),
				"dust_" + key), item_static());
		recipe.shape("abc", "def", "ghi");
		e = 0;
		for(Material m : recipe_matrix) {
			if(m != null) recipe.setIngredient((char) ('a' + e), m);
			e++;
		}
		Configuration.updateRecipe(recipe);
		return recipe;
	}
	
	public String key() {
		return key;
	}
	
	public boolean allow(Extractor ex) {
		return allowed.contains(ex);
	}
	
	public boolean is(ItemStack item) {
		if(item == null || item.getType() != material) return false;
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer p = meta.getPersistentDataContainer();
		String item_key = p.get(DustRegistry.key_dust, PersistentDataType.STRING);
		return item_key == null ? false : key.equals(item_key);
	}
	
	public ItemStack item_static() {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name.replace("%percent%", "" + percent));
		meta.setLore(info);
		if(glint == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		meta.addItemFlags(ItemFlag.values());
		meta.setCustomModelData(model);
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack item(int percent) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name.replace("%percent%", "" + percent));
		meta.setLore(info);
		if(glint == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		meta.addItemFlags(ItemFlag.values());
		meta.setCustomModelData(model);
		PersistentDataContainer p = meta.getPersistentDataContainer();
		p.set(DustRegistry.key_dust, PersistentDataType.STRING, key);
		p.set(DustRegistry.key_percent, PersistentDataType.INTEGER, percent);
		p.set(ExtractorRegistry.key_random, PersistentDataType.INTEGER, Utils.random());
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack[] items(int percent, int a) {
		ItemStack[] items = new ItemStack[a];
		while(--a >= 0) items[a] = item(percent);
		return items;
	}
	
	public void update(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer p = meta.getPersistentDataContainer();
		int percent = p.getOrDefault(DustRegistry.key_percent, PersistentDataType.INTEGER, 0);
		meta.setDisplayName(name.replace("%percent%", "" + percent));
		item.setItemMeta(meta);
	}

}
