package mc.rellox.extractableenchantments.configuration.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.api.configuration.IFile;
import mc.rellox.extractableenchantments.configuration.AbstractFile;
import mc.rellox.extractableenchantments.configuration.Settings;
import mc.rellox.extractableenchantments.text.Text;

public final class SettingsFile extends AbstractFile {

	public SettingsFile() {
		super("configuration");
	}

	@Override
	protected void initialize() {
		convert();

		defaulted("Sounds.extract-success", List.of("ENTITY_ENDER_DRAGON_FLAP:1.0:0",
				"ENTITY_EXPERIENCE_ORB_PICKUP:1.0:0"));
		defaulted("Sounds.extract-fail", List.of("ENTITY_WITHER_HURT:1.0:0.5"));
		defaulted("Sounds.extract-destroy", List.of("ENTITY_ENDER_DRAGON_HURT:1.0:0.5"));
		defaulted("Sounds.warning", List.of("BLOCK_NOTE_BLOCK_BASS:1.0:1.0"));
		defaulted("Sounds.book-fail", List.of("ENTITY_WITHER_HURT:0.85:0.5",
				"ENTITY_ELDER_GUARDIAN_DEATH:1.0:0.5"));
	    defaulted("Sounds.dust-split", List.of("BLOCK_WOOL_BREAK:1:1.5",
	    		"BLOCK_NOTE_BLOCK_CHIME:0.5:2"));
	    defaulted("Sounds.dust-combine", List.of("BLOCK_NOTE_BLOCK_CHIME:1:1.5"));
	    defaulted("Sounds.dust-apply", List.of("BLOCK_NOTE_BLOCK_CHIME:1:2",
	    		"BLOCK_FIRE_EXTINGUISH:0.75:2"));

		String path = "Extractors.default";
		
		defaulted(path + ".item.material", "NETHER_BRICK");
		defaulted(path + ".item.name", "<#00ffff-#80ccff>Enchantment Extractor");
		defaulted(path + ".item.info", List.of(
				"  <#74a6a6><!italic>Drag-and-drop onto an item",
				"<#74a6a6><!italic>to remove a random enchantment!"));
		defaulted(path + ".item.glint", true);
		defaulted(path + ".item.model", 0);
		
		defaulted(path + ".chance.enabled", false);
	    defaulted(path + ".chance.destroy", false);
	    defaulted(path + ".chance.minimum", 1);
	    defaulted(path + ".chance.maximum", 100);
	    
	    defaulted(path + ".clear-book", false);

	    defaulted(path + ".price.enabled", false);
	    defaulted(path + ".price.type", "EXPERIENCE_POINTS");
	    defaulted(path + ".price.material", "GOLD_INGOT");
	    defaulted(path + ".price.value", 32);

	    defaulted(path + ".extract.unsafe", true);
	    defaulted(path + ".extract.type", "RANDOM");
	    defaulted(path + ".extract.filter", "ALL");
	    defaulted(path + ".extract.ignored.enchantments", List.of());
	    defaulted(path + ".extract.ignored.invert", false);
	    
	    defaulted(path + ".constraints", List.of());
		defaulted(path + ".stackable", false);
	    
	    defaulted(path + ".override-chance.enabled", false);
	    defaulted(path + ".override-chance.value", 100);
	    
	    defaulted(path + ".recipe.enabled", true);
	    defaulted(path + ".recipe.ingredients", List.of(
	    		"EXPERIENCE_BOTTLE", "LAPIS_BLOCK", "EXPERIENCE_BOTTLE",
	    		"LAPIS_BLOCK", "BOOK", "LAPIS_BLOCK",
	    		"EXPERIENCE_BOTTLE", "LAPIS_BLOCK", "EXPERIENCE_BOTTLE"));

		path = "Dust.default";
		
		defaulted(path + ".item.material", "SUGAR");
		defaulted(path + ".item.name", "<#ffff00-#ffbf00>Dust of Chance <#00ffff>(%percent%%)");
		defaulted(path + ".item.info", List.of(
				"  <#cc6652><!italic>Drag-and-drop onto an extractor",
				"<#cc6652><!italic>to increase its extraction chance!",
				"",
				"  <#bf7373><!italic>Shift-right-click to split in half."));
		defaulted(path + ".item.glint", true);
		defaulted(path + ".item.model", 0);
		
		defaulted(path + ".applicable-to.extractors", List.of("default"));
		defaulted(path + ".applicable-to.books", false);
		
		defaulted(path + ".limit", 0);
	    defaulted(path + ".percent", 10);
		
	    defaulted(path + ".recipe.enabled", true);
	    defaulted(path + ".recipe.ingredients", List.of(
	    		"empty", "REDSTONE", "empty",
	    		"REDSTONE", "GLOWSTONE", "REDSTONE",
	    		"empty", "REDSTONE", "empty"));
	    
	    defaulted("Books.chance.enabled", false);
	    defaulted("Books.chance.minimum", 1);
	    defaulted("Books.chance.maximum", 100);
	    
	    defaulted("Anvils.apply-books", true);
	    defaulted("Anvils.apply-unsafe", false);
	    defaulted("Anvils.apply-restrictions", new ArrayList<>());
	    
	    defaulted("Items.extractor-layout", List.of(
	    		"INFO",
	    		"!",
	    		"CHANCE",
	    		"DESTROY",
	    		"!",
	    		"PRICE"));
	    defaulted("Items.dust-layout", List.of(
	    		"INFO"));
	    
	    header("In this file you can create your own",
	    		"  enchantment extractors and dust.");
	    
	    Commenter c = commenter();
	    if(c != null) {
	    	c.comment("Sounds",
	    			"Sounds for each action.",
	    			"Sound format:",
	    			"- <sound>:<volume>:<pitch>",
	    			"(volume intarval [0-1])",
	    			"(pitch intarval [0-2])",
	    			"Setting the sounds to an empty",
	    			"  list '[]' will not make any sound.");
	    	
	    	c.comment("Extractors.default",
	    			"This is the default extractor.",
	    			"To create new extractors just copy",
	    			"  this deafult extractor and then change",
	    			"  the key 'default' to your new extractor.",
	    			"(Keys must only contain latin leters,",
	    			"  numbers and underscores '_')",
	    			"After that you can configure the new",
	    			"  extractor to your liking.");
	    	
	    	c.comment("Extractors.default.item.material",
	    			"Material type for this extractor item.");
	    	c.comment("Extractors.default.item.name",
	    			"Name for this extractor item.");
	    	c.comment("Extractors.default.item.info",
	    			"Info for this extractor item.");
	    	c.comment("Extractors.default.item.glint",
	    			"Does this extractor item has glint.");
	    	c.comment("Extractors.default.item.model",
	    			"Model data for this extractor item.",
	    			"Useful if you are using a resource pack.");

	    	c.comment("Extractors.default.chance.enabled",
	    			"Is extraction chance enabled.");
	    	c.comment("Extractors.default.chance.destroy",
	    			"Should the enchantment be destroyed",
	    			"  if it fails to extract.");
	    	c.comment("Extractors.default.chance.minimum",
	    			"Minimum chance when crafting.");
	    	c.comment("Extractors.default.chance.maximum",
	    			"Maximum chance when crafting.");
	    	
	    	c.comment("Extractors.default.clear-book",
	    			"If true then this extractor will never",
	    			"  give back an enchantment book,",
	    			"  even when it succeeds.");
	    	
	    	c.comment("Extractors.default.price.enabled",
	    			"Is extraction price enabled.");
	    	c.comment("Extractors.default.price.type",
	    			"Extraction price type.",
	    			"There are 4 price types:",
	    			"- EXPERIENCE_POINTS",
	    			"- EXPERIENCE_LEVELS",
	    			"- MATERIALS",
	    			"- ECONOMY (for Vault money)");
	    	c.comment("Extractors.default.price.material",
	    			"Price material type.",
	    			"Only if price type is MATERIALS.");
	    	c.comment("Extractors.default.price.value",
	    			"Price value to remove.");
	    	
	    	c.comment("Extractors.default.extract.unsafe",
	    			"Can unsafe enchantments be extracted.",
	    			"Unsafe enchantments are enchantments that",
	    			"  exceeds the maximum level.",
	    			"(e.g. Mending III, Fortune IV...)");
	    	c.comment("Extractors.default.extract.type",
	    			"The extraction type.",
	    			"There are 2 extraction types:",
	    			"- RANDOM (extracts a random enchantment)",
	    			"- SELECTION (choose the enchantment to extract)");
	    	c.comment("Extractors.default.extract.filter",
	    			"The enchantment extraction filter.",
	    			"There are 3 extraction filters:",
	    			"- ALL (extracts any enchantment)",
	    			"- MINECRAFT (extracts only minecraft enchantments)",
	    			"- CUSTOM (extracts only custom enchantments)");
	    	c.comment("Extractors.default.extract.ignored.enchantments",
	    			"List of enchantment keys that should not be extracted.");
	    	c.comment("Extractors.default.extract.ignored.invert",
	    			"Inverts the ignored enchantment list.",
	    			"If true, the extractor will only allow to",
	    			"  extract enchantments that are contained",
	    			"  in the list.");
	    	
	    	c.comment("Extractors.default.constraints",
	    			"Extractor item constaints.",
	    			"If any constrain matches the item then",
	    			"  it will not allow to extract.",
	    			"There are 6 constraint types:",
	    			"- ITEM_BY_MATERIAL:<material>",
	    			"- ITEM_WITH_NAME:<name?>",
	    			"- ITEM_WITH_LORE:<lore line?>",
	    			"- ITEM_WITH_MODEL:<model?>",
	    			"- ITEM_WITH_FLAG:<flag?>",
	    			"- ITEM_UNBREAKABLE",
	    			"Any parameter with '?' at the end is optional,",
	    			"  meaning you can set it like:",
	    			"- ITEM_WITH_NAME:Custom Name",
	    			"  (will deny only items with the name 'Custom Name')",
	    			"and",
	    			"- ITEM_WITH_NAME",
	    			"  (will deny items with any custom set name)");
	    	
	    	c.comment("Extractors.default.stackable",
	    			"Can extractors be stacked.",
	    			"This does not work if chance is enabled.");
	    	
	    	c.comment("Extractors.default.override-chance.enabled",
	    			"Will this extractor override the default",
	    			"  book chance.",
	    			"This only applies if book chance is enabled.");
	    	c.comment("Extractors.default.override-chance.value",
	    			"The overriden chance value for the extracted book.");
	    	
	    	c.comment("Extractors.default.recipe.enabled",
	    			"Does this extractor has a recipe.");
	    	c.comment("Extractors.default.recipe.ingredients",
	    			"List of ingredients.",
	    			"This list cannot have less or more than 9 materials.",
	    			"Empty spaces must be replaced with 'empty'.",
	    			"For the recipe to use multiple items for a single extractor",
	    			"  you can use: <material>:<amount>",
	    			"  (e.g. BOOK:16)");

	    	c.comment("Dust.default",
	    			"This is the default dust.",
	    			"To create new dust just copy",
	    			"  this deafult dust and then change",
	    			"  the key 'default' to your new dust.",
	    			"(Keys must only contain latin leters,",
	    			"  numbers and underscores '_')",
	    			"After that you can configure the new",
	    			"  dust to your liking.");
	    	
	    	c.comment("Dust.default.item.material",
	    			"Material type for this extractor item.");
	    	c.comment("Dust.default.item.name",
	    			"Name for this extractor item.");
	    	c.comment("Dust.default.item.info",
	    			"Info for this extractor item.");
	    	c.comment("Dust.default.item.glint",
	    			"Does this extractor item has glint.");
	    	c.comment("Dust.default.item.model",
	    			"Model data for this extractor item.",
	    			"Useful if you are using a resource pack.");
	    	
	    	c.comment("Dust.default.applicable-to.extractors",
	    			"List of extractor keys that this dust can be applied.");
	    	c.comment("Dust.default.applicable-to.books",
	    			"If this dust can be applied to extracted books.");
	    	
	    	c.comment("Dust.default.limit",
	    			"The limit one dust item can hold.",
	    			"Set 0 for no limit.");
	    	c.comment("Dust.default.percent",
	    			"The percent set to the dust when crafting.");
	    	
	    	c.comment("Dust.default.recipe.enabled",
	    			"Does this extractor has a recipe.");
	    	c.comment("Dust.default.recipe.ingredients",
	    			"List of ingredients.",
	    			"This list cannot have less or more than 9 materials.",
	    			"Empty spaces must be replaced with 'empty'.",
	    			"For the recipe to use multiple items for a single extractor",
	    			"  you can use: <material>:<amount>",
	    			"  (e.g. BOOK:16)");
	    	
	    	c.comment("Books.chance.enabled",
	    			"Does extracted books have a chance.");
	    	c.comment("Books.chance.minimum",
	    			"Book minimum chance.");
	    	c.comment("Books.chance.maximum",
	    			"Book maximum chance.");
	    	
	    	c.comment("Anvils.apply-books",
	    			"Can books be applied to items using an anvil.");
	    	c.comment("Anvils.apply-unsafe",
	    			"Can unsafe books be applied to items using an anvil.");
	    	c.comment("Anvils.apply-restrictions",
	    			"Anvil applying restrictions:",
	    			"- BOOK_TO_BOOK",
	    			"- BOOK_TO_ARMOR",
	    			"- BOOK_TO_WEAPON",
	    			"- BOOK_TO_SWORD",
	    			"- BOOK_TO_ARCHERY",
	    			"- BOOK_TO_TOOL",
	    			"- BOOK_TO_MISC",
	    			"- ARMOR_TO_ARMOR",
	    			"- WEAPON_TO_WEAPON",
	    			"- ARCHERY_TO_ARCHERY",
	    			"- SWORD_TO_SWORD",
	    			"- TOOL_TO_TOOL",
	    			"- MISC_TO_MISC");
	    	
	    	c.comment("Items.extractor-layout",
	    			"The layout for an extractor.",
	    			"Keys: INFO, CHANCE, DESTROY, PRICE",
	    			"Leave '!' for an empty lore line.");
	    	c.comment("Items.dust-layout",
	    			"The layout for a dust.",
	    			"Keys: INFO",
	    			"Leave '!' for an empty lore line.");
	    }
	    
	    save();
	    
	    Settings.load();
	}
	
	private void convert() {
		File old_file = new File(ExtractableEnchantments.instance().getDataFolder(), "config.yml");
		if(old_file.exists() == false) return;
		
		FileConfiguration old = YamlConfiguration.loadConfiguration(old_file);
		
		{
			Mover m = new Mover(this, old);
			
			String s = old.getString("Settings.Sounds.ExtractionVolume");
			hold("Sounds.extract-success", List.of("ENTITY_ENDER_DRAGON_FLAP:" + s + ":0",
					"ENTITY_EXPERIENCE_ORB_PICKUP:" + s + ":0"));
			hold("Sounds.extract-fail", List.of("ENTITY_WITHER_HURT:" + s + ":0.5"));
			hold("Sounds.extract-destroy", List.of("ENTITY_ENDER_DRAGON_HURT:" + s + ":0.5"));

			m.move("Book.Chance.Toggle", "Books.chance.enabled");
			m.move("Book.Chance.Min", "Books.chance.minimum");
			m.move("Book.Chance.Max", "Books.chance.maximum");
			
			m.move("Anvil.Apply.Books", "Anvils.apply-books");
			m.move("Anvil.Apply.Unfase", "Anvils.apply-unsafe");
			m.move("Anvil.Apply.Restrict", "Anvils.apply-restrictions");
		}
		
		ConfigurationSection cs = old.getConfigurationSection("Extractors");
		if(cs != null) {
			Mover m = new Mover(this, cs);
			Set<String> keys = cs.getKeys(false);
			keys.forEach(key -> {
				String path = "Extractors." + key;
				m.move(key + ".Material", path + ".item.material");
				m.move(key + ".Name", path + ".item.name", Mover.text);
				m.move(key + ".Info", path + ".item.info", Mover.text);
				m.move(key + ".Glint", path + ".item.glint");
				m.move(key + ".CustomModelData", path + ".item.model");
				
				m.move(key + ".Chance.Toggle", path + ".chance.enabled");
				m.move(key + ".Chance.Destroy", path + ".chance.destroy");
				m.move(key + ".Chance.Min", path + ".chance.minimum");
				m.move(key + ".Chance.Max", path + ".chance.maximum");
				
				m.move(key + ".Cost.Toggle", path + ".price.enabled");
				m.move(key + ".Cost.Type", path + ".price.type");
				m.move(key + ".Cost.Material", path + ".price.material");
				m.move(key + ".Cost.Value", path + ".price.value");
				
				m.move(key + ".Extract.Unsafe", path + ".extract.unsafe");
				m.move(key + ".Filter", path + ".extract.filter");
				m.move(key + ".Extraction", path + ".extract.type");
				m.move(key + ".IgnoredEnchantments", path + ".extract.ignored.enchantments");
				
				m.move(key + ".Constraints", path + ".constraints", Mover.constraints);
				m.move(key + ".Stackable", path + ".stackable");
				
				m.move(key + ".Book.Chance.Force", path + ".override-chance.enabled");
				m.move(key + ".Book.Chance.Value", path + ".override-chance.value");
				
				m.move(key + ".Recipe.Toggle", path + ".recipe.enabled");
				m.move(key + ".Recipe.Matrix", path + ".recipe.ingredients", Mover.matrix);
			});
		}
		cs = old.getConfigurationSection("Dusts");
		if(cs != null) {
			Mover m = new Mover(this, cs);
			Set<String> keys = cs.getKeys(false);
			keys.forEach(key -> {
				String path = "Dust." + key;
				m.move(key + ".Material", path + ".item.material");
				m.move(key + ".Name", path + ".item.name", Mover.text);
				m.move(key + ".Info", path + ".item.info", Mover.text);
				m.move(key + ".Glint", path + ".item.glint");
				m.move(key + ".CustomModelData", path + ".item.model");
				
				m.move(key + ".Allowed", path + ".applicable-to.extractors");
				m.move(key + ".Books", path + ".applicable-to.books");
				
				m.move(key + ".Limit", path + ".limit");

				m.move(key + ".Recipe.Toggle", path + ".recipe.enabled");
				m.move(key + ".Recipe.Percent", path + ".percent");
				m.move(key + ".Recipe.Matrix", path + ".recipe.ingredients", Mover.matrix);
			});
		}
		
		old_file.delete();
	}
	
	private record Mover(IFile file, ConfigurationSection s) {
		
		static final Function<Object, Object> text = o -> {
			if(o instanceof String s) return Text.fromLegacy(s);
			if(o instanceof List list) {
				@SuppressWarnings("unchecked")
				List<Object> i = list;
				return Text.fromLegacy(i.stream()
						.map(String::valueOf)
						.toList());
			}
			return null;
		};
		static final Function<Object, Object> matrix = o -> {
			if(o instanceof List list) {
				@SuppressWarnings("unchecked")
				List<String> s = list;
				for(int i = 0; i < s.size(); i++) {
					if(s.get(i).equalsIgnoreCase("EMPTY") == false) continue;
					s.set(i, "empty");
				}
				return s;
			}
			return null;
		};
		static final Function<Object, Object> constraints = o -> {
			if(o instanceof List list) {
				@SuppressWarnings("unchecked")
				List<Object> i = list;
				List<String> l = i.stream()
						.map(String::valueOf)
						.collect(Collectors.toList());
				l.replaceAll(s -> {
					if(s.equalsIgnoreCase("ITEM_WITH_FLAGS") == true)
						return "ITEM_WITH_FLAG";
					return s;
				});
				return l;
			}
			return null;
		};
		
		void move(String from, String to) {
			file.hold(to, s.get(from));
		}
		
		<A> void move(String from, String to, Function<A, ?> parser) {
			@SuppressWarnings("unchecked")
			A value = (A) s.get(from);
			file.hold(to, parser.apply(value));
		}
	}

}
