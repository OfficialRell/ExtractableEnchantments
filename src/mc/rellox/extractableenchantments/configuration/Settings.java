package mc.rellox.extractableenchantments.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;

import mc.rellox.extractableenchantments.api.anvil.Restriction;
import mc.rellox.extractableenchantments.api.configuration.IFile;
import mc.rellox.extractableenchantments.api.extractor.IChanceOverride;
import mc.rellox.extractableenchantments.api.extractor.IExtractPrice;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.extractor.IExtractorChance;
import mc.rellox.extractableenchantments.api.extractor.constraint.IConstraint;
import mc.rellox.extractableenchantments.api.extractor.extract.ExtractFilter;
import mc.rellox.extractableenchantments.api.extractor.extract.ExtractType;
import mc.rellox.extractableenchantments.api.extractor.extract.IAccepted;
import mc.rellox.extractableenchantments.api.extractor.extract.IIgnoredEnchantment;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipe;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipe.RecipeItem;
import mc.rellox.extractableenchantments.api.price.IPrice;
import mc.rellox.extractableenchantments.api.price.PriceType;
import mc.rellox.extractableenchantments.api.utility.ISound;
import mc.rellox.extractableenchantments.configuration.Configuration.CF;
import mc.rellox.extractableenchantments.dust.Applicable;
import mc.rellox.extractableenchantments.dust.Dust;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.extractor.ChanceOverride;
import mc.rellox.extractableenchantments.extractor.ExtractPrice;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.extractor.ExtractorChance;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.extractor.constraint.Constraint;
import mc.rellox.extractableenchantments.extractor.extract.Extract;
import mc.rellox.extractableenchantments.extractor.extract.ExtractAccepted;
import mc.rellox.extractableenchantments.item.DustItem;
import mc.rellox.extractableenchantments.item.ExtractorItem;
import mc.rellox.extractableenchantments.item.order.OrderList;
import mc.rellox.extractableenchantments.item.recipe.ItemRecipe;
import mc.rellox.extractableenchantments.price.Price.PriceEconomy;
import mc.rellox.extractableenchantments.price.Price.PriceLevels;
import mc.rellox.extractableenchantments.price.Price.PriceMaterials;
import mc.rellox.extractableenchantments.price.Price.PricePoints;
import mc.rellox.extractableenchantments.text.Text;
import mc.rellox.extractableenchantments.text.content.Content;
import mc.rellox.extractableenchantments.text.content.ContentParser;
import mc.rellox.extractableenchantments.utility.Utility;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public final class Settings {
	
	public static final Settings settings = new Settings();
	
	public static void load() {
		settings.load0();
	}
	
	public ISound sound_extract_success;
	public ISound sound_extract_fail;
	public ISound sound_extract_destroy;
	public ISound sound_warning;
	public ISound sound_book_fail;
	public ISound sound_dust_split;
	public ISound sound_dust_combine;
	public ISound sound_dust_apply;
	
	public boolean book_chance_enabled;
	public int book_chance_minimum;
	public int book_chance_maximum;
	
	public boolean anvils_apply_books;
	public boolean anvils_apply_unsafe;
	public final List<Restriction> anvils_apply_restrictions;
	
	public OrderList order_extractor, order_dust;
	
	public int extraction_selection_rows;
	
	private Settings() {
		this.anvils_apply_restrictions = new ArrayList<>();
	}
	
	private void load0() {
		IFile file = CF.s;
		
		sound_extract_success = ISound.fetch(file, "extract-success");
		sound_extract_fail = ISound.fetch(file, "extract-fail");
		sound_extract_destroy = ISound.fetch(file, "extract-destroy");
		sound_warning = ISound.fetch(file, "warning");
		sound_book_fail = ISound.fetch(file, "book-fail");
		sound_dust_split = ISound.fetch(file, "dust-split");
		sound_dust_combine = ISound.fetch(file, "dust-combine");
		sound_dust_apply = ISound.fetch(file, "dust-apply");
		
		book_chance_enabled = file.getBoolean("Books.chance.enabled");
		book_chance_minimum = file.getInteger("Books.chance.minimum");
		book_chance_maximum = file.getInteger("Books.chance.maximum");
		
		anvils_apply_books = file.getBoolean("Anvils.apply-books");
		anvils_apply_unsafe = file.getBoolean("Anvils.apply-unsafe");
		RF.enumerates(Restriction.class, file.getStrings("Anvils.apply-restrictions"))
		.forEach(anvils_apply_restrictions::add);
		
		order_extractor = new OrderList(file.getStrings("Items.extractor-layout"));
		order_dust = new OrderList(file.getStrings("Items.dust-layout"));
		
		extraction_selection_rows = file.getInteger("Extraction.selection.rows", 1, 5);
		
		file.keys("Extractors").forEach(key -> extractor(file, key));
		file.keys("Dust").forEach(key -> dust(file, key));
	}
	
	private static void extractor(IFile file, String key) {
		if(Text.key(key) == false) Text.logFail("Invalid extractor key: " + key);
		String path = "Extractors." + key;
		
		try {
			Material material = RF.enumerate(Material.class, file.getString(path + ".item.material"));
			List<Content> name = ContentParser.parse(file.getStringsAll(path + ".item.name"));
			List<Content> info = ContentParser.parse(file.getStringsAll(path + ".item.info"));
			boolean glint = file.getBoolean(path + ".item.glint");
			int model = file.getInteger(path + ".item.model");
			String tooltip = file.getString(path + ".item.tooltip");
			if(tooltip != null && tooltip.isEmpty() == true) tooltip = null;
			ExtractorItem item = new ExtractorItem(material, name, info, glint, model, tooltip);

			IExtractorChance chance;
			if(file.getBoolean(path + ".chance.enabled") == true) {
				boolean destroy = file.getBoolean(path + ".chance.destroy");
				int minimum = file.getInteger(path + ".chance.minimum");
				int maximum = file.getInteger(path + ".chance.maximum");
				chance = new ExtractorChance(destroy, minimum, maximum);
			} else chance = IExtractorChance.empty;

			boolean clearing = file.getBoolean(path + ".clear-book");
			
			boolean override_enabled = file.getBoolean(path + ".override-chance.enabled");
			int value = file.getInteger(path + ".override-chance.value");
			IChanceOverride override = new ChanceOverride(override_enabled, value);
			
			boolean unsafe = file.getBoolean(path + ".extract.unsafe");
			ExtractType extract_type = RF.enumerate(ExtractType.class, file.getString(path + ".extract.type"));
			ExtractFilter filter = RF.enumerate(ExtractFilter.class, file.getString(path + ".extract.filter"));
			
			List<String> strings = file.getStrings(path + ".extract.ignored.enchantments");
			strings.replaceAll(String::toLowerCase);
			
			Set<IIgnoredEnchantment> enchantments = strings.stream().map(s -> {
				if(s.indexOf(':') > 0) {
					String[] ss = s.split(":");
					String k = ss[0];
					String v = ss[1];
					if(s.matches(".+:\\d+") == true) {
						int level = Integer.parseInt(v);
						return IIgnoredEnchantment.of(k, level);
					}
					if(s.matches(".+:\\d+\\+") == true) {
						int minumum = Integer.parseInt(v.replace("+", ""));
						return IIgnoredEnchantment.of(k, minumum, Integer.MAX_VALUE);
					}
					if(s.matches(".+:-\\d+") == true) {
						int maximum = Integer.parseInt(v.replace("-", ""));
						return IIgnoredEnchantment.of(k, 0, maximum);
					}
					if(s.matches(".+:\\d+-\\d+") == true) {
						String[] sss = v.split("-");
						int minumum = Integer.parseInt(sss[0]);
						int maximum = Integer.parseInt(sss[1]);
						return IIgnoredEnchantment.of(k, minumum, maximum);
					}
				}
				return IIgnoredEnchantment.of(s);
			})
			.collect(Collectors.toSet());
			
			boolean invert = file.getBoolean(path + ".extract.ignored.invert");
			IAccepted accepted = new ExtractAccepted(enchantments, invert);
			
			Extract extract = new Extract(unsafe, extract_type, filter, accepted);
			
			List<IConstraint> constraints = file.getStrings(path + ".constraints").stream()
					.map(Constraint::of)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			
			IExtractPrice extract_price;
			if(file.getBoolean(path + ".price.enabled") == true) {
				PriceType price_type = RF.enumerate(PriceType.class,
						file.getString(path + ".price.type"), PriceType.EXPERIENCE_POINTS);
				int price_value = file.getInteger(path + ".price.value");
				
				IPrice price = switch (price_type) {
				case EXPERIENCE_POINTS -> new PricePoints(price_value);
				case EXPERIENCE_LEVELS -> new PriceLevels(price_value);
				case MATERIALS -> {
					Material price_material = RF.enumerate(Material.class,
							file.getString(path + ".price.material"), Material.GOLD_INGOT);
					yield new PriceMaterials(price_value, price_material);
				}
				case ECONOMY -> new PriceEconomy(price_value);
				};
				extract_price = new ExtractPrice(price);

			} else extract_price = IExtractPrice.empty;
			
			IRecipe recipe;
			if(file.getBoolean(path + ".recipe.enabled") == true) {
				List<RecipeItem> ingredients = file.getStrings(path + ".recipe.ingredients")
						.stream()
						.map(IRecipe::of)
						.collect(Collectors.toList());
				recipe = new ItemRecipe(ingredients);
			} else recipe = IRecipe.empty;
			
			boolean stackable = file.getBoolean(path + ".stackable");
			
			Extractor extractor = new Extractor(key, item, chance, clearing, override, extract, extract_price, constraints, recipe, stackable);
			
			item.extractor = extractor;
			if(recipe instanceof ItemRecipe ir) ir.object = extractor;
			recipe.update();
			
			ExtractorRegistry.add(extractor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void dust(IFile file, String key) {
		if(Text.key(key) == false) Text.logFail("Invalid dust key: " + key);
		String path = "Dust." + key;
		
		try {
			Material material = RF.enumerate(Material.class, file.getString(path + ".item.material"));
			List<Content> name = ContentParser.parse(file.getStringsAll(path + ".item.name"));
			List<Content> info = ContentParser.parse(file.getStringsAll(path + ".item.info"));
			boolean glint = file.getBoolean(path + ".item.glint");
			int model = file.getInteger(path + ".item.model");
			String tooltip = file.getString(path + ".item.tooltip");
			if(tooltip != null && tooltip.isEmpty() == true) tooltip = null;
			DustItem item = new DustItem(material, name, info, glint, model, tooltip);
			
			Set<IExtractor> extractors = file.getStrings(path + ".applicable-to.extractors").stream()
					.map(ExtractorRegistry::get)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
			boolean books = file.getBoolean(path + ".applicable-to.books");
			
			Applicable applicable = new Applicable(extractors, books);

			int limit = file.getInteger(path + ".limit");
			if(limit <= 0) limit = Integer.MAX_VALUE;
			
			IRecipe recipe;
			if(file.getBoolean(path + ".recipe.enabled") == true) {
				List<RecipeItem> ingredients = file.getStrings(path + ".recipe.ingredients")
						.stream()
						.map(IRecipe::of)
						.collect(Collectors.toList());
				recipe = new ItemRecipe(ingredients);
			} else recipe = IRecipe.empty;
			
			int percent = file.getInteger(path + ".percent");
			
			Dust dust = new Dust(key, item, applicable, recipe, limit, percent);
			
			item.dust = dust;
			if(recipe instanceof ItemRecipe ir) ir.object = dust;
			recipe.update();
			
			DustRegistry.add(dust);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int book() {
		return Utility.between(settings.book_chance_minimum, settings.book_chance_maximum);
	}
	
	public static boolean restricted(Material what, Material to) {
		return settings.anvils_apply_restrictions.stream()
				.anyMatch(r -> r.restricted(what, to));
	}

}
