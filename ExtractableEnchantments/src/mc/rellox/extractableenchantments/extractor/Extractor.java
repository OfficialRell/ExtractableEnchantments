package mc.rellox.extractableenchantments.extractor;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.Constraint;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.Extract;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry.ExtractionType;
import mc.rellox.extractableenchantments.usage.Cost;
import mc.rellox.extractableenchantments.usage.CostType;
import mc.rellox.extractableenchantments.utils.Utils;

public final class Extractor {

	public final String key;
	
	public final Material material;
	public final String name;
	public final List<String> info;
	
	public final int model;
	public final boolean glint;
	public final boolean chance_toggle;
	public final boolean chance_destroy;
	public final int chance_min;
	public final int chance_max;
	
	public final boolean cost_toggle;
	public final CostType cost_type;
	public final Material cost_material;
	public final int cost_value;

	public final boolean book_chance_force;
	public final int book_chance_value;
	
	public final boolean extract_unsafe;
	
	public final Set<String> ignored_enchantments;
	
	public final ExtractionType extraction;
	public final Extract extract;
	public final Constraint[] constraints;
	
	public final boolean recipe_toggle;
	public final Material[] recipe_matrix;
	public final ShapedRecipe recipe;
	
	public Extractor(String key, Material material, String name, List<String> info,
			boolean glint, int model, boolean chance_toggle, boolean chance_destroy, int chance_min, int chance_max,
			boolean cost_toggle, CostType cost_type, Material cost_material, int cost_value,
			boolean book_chance_force, int book_chance_value, boolean extract_unsafe, List<String> ignored_enchantments,
			ExtractionType extraction, Extract extract, Constraint[] constraints, boolean recipe_toggle, Material[] recipe_matrix) {
		this.key = key;
		
		this.material = material;
		this.name = name;
		this.info = info;
		
		this.glint = glint;
		this.model = model;
		this.chance_toggle = chance_toggle;
		this.chance_destroy = chance_destroy;
		this.chance_min = chance_min;
		this.chance_max = chance_max;
		
		this.cost_toggle = cost_toggle;
		this.cost_type = cost_type;
		this.cost_material = cost_material;
		this.cost_value = cost_value;
		
		this.book_chance_force = book_chance_force;
		this.book_chance_value = book_chance_value;

		this.extract_unsafe = extract_unsafe;
		
		this.ignored_enchantments = ignored_enchantments.stream()
				.map(String::toLowerCase)
				.collect(Collectors.toSet());
		
		this.extraction = extraction;
		this.extract = extract;
		this.constraints = constraints;
		
		this.recipe_toggle = recipe_toggle;
		this.recipe_matrix = recipe_matrix;
		this.recipe = loadRecipe();
	}
	
	private ShapedRecipe loadRecipe() {
		if(recipe_toggle == false) return null;
		int e = 0;
		for(Material m : recipe_matrix) if(m == null) e++;
		if(e == 9) return null;
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(ExtractableEnchantments.instance(),
				"extractor_" + key), item_static());
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
		return this.key;
	}
	
	public boolean chance(ItemStack item) {
		if(chance_toggle == false) return true;
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer p = meta.getPersistentDataContainer();
		return Utils.random(100) <= p.getOrDefault(DustRegistry.key_chance, PersistentDataType.INTEGER, 100);
	}
	
	public Cost cost() {
		return cost_type.cost(this, cost_value);
	}
	
	public boolean is(ItemStack item) {
		if(item == null || item.getType() != material) return false;
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer p = meta.getPersistentDataContainer();
		String item_key = p.get(ExtractorRegistry.key_extractor, PersistentDataType.STRING);
		return item_key == null ? false : key.equals(item_key);
	}
	
	public Predicate<Enchantment> filter() {
		return extract.filter().and(this::allow);
	}
	
	public boolean ignore(ItemStack item) {
		if(constraints == null) return false;
		ItemMeta meta = item.getItemMeta();
		return Stream.of(constraints).anyMatch(c -> c.ignore(meta));
	}
	
	@SuppressWarnings("deprecation")
	private boolean allow(Enchantment e) {
		if(ignored_enchantments.isEmpty() == true) return true;
		if(ignored_enchantments.contains(e.getKey().getKey()) == true) return false;
		if(ignored_enchantments.contains(e.getName()) == true) return false;
		return ignored_enchantments.contains(e.getName().toLowerCase()) == false;
	}
	
	public ItemStack item_static() {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if(glint == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		meta.addItemFlags(ItemFlag.values());
		meta.setCustomModelData(model);
		String lore_chance = null, lore_destroy = null, lore_cost = null;
		if(chance_toggle == true) {
			lore_chance = Language.extractor_lore_chance("???");
			if(chance_destroy == true) lore_destroy = Language.extractor_lore_destroy();
		}
		if(cost_toggle == true) {
			Cost c = cost_type.cost(this, cost_value);
			lore_cost = Language.extractor_lore_cost(c);
		}
		meta.setLore(Language.item_layout().build(info, lore_chance, lore_destroy, lore_cost));
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack item() {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if(glint == true) meta.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		meta.addItemFlags(ItemFlag.values());
		meta.setCustomModelData(model);
		String lore_chance = null, lore_destroy = null, lore_cost = null;
		int chance = 0;
		if(chance_toggle == true) {
			chance = Utils.between(chance_min, chance_max);
			lore_chance = Language.extractor_lore_chance(chance);
			if(chance_destroy == true) lore_destroy = Language.extractor_lore_destroy();
		}
		if(cost_toggle == true) {
			Cost c = cost_type.cost(this, cost_value);
			lore_cost = Language.extractor_lore_cost(c);
		}
		meta.setLore(Language.item_layout().build(info, lore_chance, lore_destroy, lore_cost));
		PersistentDataContainer p = meta.getPersistentDataContainer();
		p.set(ExtractorRegistry.key_extractor, PersistentDataType.STRING, key);
		p.set(ExtractorRegistry.key_random, PersistentDataType.INTEGER, Utils.random());
		p.set(DustRegistry.key_chance, PersistentDataType.INTEGER, chance);
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack[] items(int a) {
		ItemStack[] items = new ItemStack[a];
		while(--a >= 0) items[a] = item();
		return items;
	}
	

}
