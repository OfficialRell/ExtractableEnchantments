package mc.rellox.extractableenchantments.configuration.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.api.configuration.IFile;
import mc.rellox.extractableenchantments.configuration.AbstractFile;
import mc.rellox.extractableenchantments.configuration.Configuration.CF;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;
import mc.rellox.extractableenchantments.text.Text;
import mc.rellox.extractableenchantments.text.content.Content;
import mc.rellox.extractableenchantments.text.content.ContentParser;

public final class LanguageFile extends AbstractFile {

	private final Map<String, List<Content>> text = new HashMap<>();
	private final List<String> keys = new ArrayList<>();

	public String color_minecraft, color_custom, color_curse;

	public LanguageFile() {
		super("language");
	}

	@Override
	protected void initialize() {
		convert();

		put("Price.experience-points.not-enough", "<#800000>(!) <#ff8000>Not enough experience points!");
		put("Price.experience-points.value", "%price% Experience Points");
		put("Price.experience-levels.not-enough", "<#800000>(!) <#ff8000>Not enough experience levels!");
		put("Price.experience-levels.value", "%price% Experience Levels");
		put("Price.materials.not-enough", "<#800000>(!) <#ff8000>Not enough materials!");
		put("Price.materials.value", "%price% " + Text.symbol_multiplier + " %material%");
		put("Price.economy.not-enough", "<#800000>(!) <#ff8000>Insufficient funds!");
		put("Price.economy.value", "$%price%");

		put("Permission.warning.craft-extractor", "<#800000>(!) <#ff8000>You do not have the permission to craft this!");
		put("Permission.warning.craft-dust", "<#800000>(!) <#ff8000>You do not have the permission to craft this!");
		put("Permission.warning.use-extractor", "<#800000>(!) <#ff8000>You do not have the permission to use this!");
		put("Permission.warning.apply-book", "<#800000>(!) <#ff8000>You do not have the permission to apply this!");
		put("Permission.warning.use-dust", "<#800000>(!) <#ff8000>You do not have the permission to apply this!");
		put("Permission.warning.split-dust", "<#800000>(!) <#ff8000>You do not have the permission to split this!");

		put("Extractor.info.chance", "<#a6a6a6>Extraction chance: <#6699ff>%chance%%");
		put("Extractor.info.price", "<#a6a6a6>Extraction price: <#66ff66>%price%");
		put("Extractor.info.destroy", "<#bf0000><!italic>Destroys enchantment on failure!");
		put("Extractor.info.unknown-chance", "???");

		put("Extraction.success", "<#008000>(!) <#00ff00>Successfully extracted %enchantment%");
		put("Extraction.fail", "<#800000>(!) <#ff8000>Extraction Failed!");
		put("Extraction.unsafe", "<#800000>(!) <#ff8000>Unable to extract unsafe enchantments!");
		put("Extraction.constraint", "<#800000>(!) <#ff8000>Cannot extract from this item!");
		put("Extraction.filter.minecraft", "<#800000>(!) <#ff8000>Unable to extract non-minecraft enchantments!");
		put("Extraction.filter.custom", "<#800000>(!) <#ff8000>Unable to extract minecraft enchantments!");
		put("Extraction.destroy", "<#800000>(!) <#ff8000>Extraction Failed! <#ff0000>Enchantment %enchantment% was destroyed!");

		put("Extraction.selection.name", "Select an enchantment");
		put("Extraction.selection.enchantment.name", "<#0080ff>Enchantment: %enchantment%");
		put("Extraction.selection.enchantment.info", "  <#808080><!italic>Click to extract");
		defaulted("Extraction.selection.color.minecraft", "#00ffff");
		defaulted("Extraction.selection.color.custom", "#00ffff");
		defaulted("Extraction.selection.color.curse", "#ff0000");

		put("Dust.split.held", "<#800000>(!) <#ff8000>Cannot split from held item!");
		put("Dust.split.invalid", "<#800000>(!) <#ff8000>Invalid amount (%amount%)!");
		put("Dust.split.too-large", "<#800000>(!) <#ff8000>Cannot split more than or equal to %value%%!");
		put("Dust.split.too-small", "<#800000>(!) <#ff8000>Cannot split from this dust!");

		put("Book.info.chance", "<#a6a6a6>Applying chance: <#6699ff>%chance%%");
		put("Book.apply.fail", "<#800000>(!) <#ff8000>Enchantment book failed to apply!");

		EnchantmentRegistry.ENCHANTMENTS.forEach((key, enchantment) -> {
			put("Enchantments." + key, enchantment.name());
		});

		header("""

				Language file has been updated!

				(!!!) Legacy colors (&a&1&b...) are NO longer available.

				New text formatting:

				Color format:
				  <#123abc>
				  <#ABC987>
				  ...

				Gradient format:
				  <#ff0000-#00ff00>
				  <#ff0000-#ffff00-#00ff00>
				  ...

				Modifier format:
				  bold - <!bold> or <!b>
				  italic - <!italic> or <!i>
				  underline - <!underline> or <!u>
				  strikethrough - <!strikethrough> or <!s>
				  obfuscated - <!obfuscated> or <!o>

				If you find any errors or bugs, or any text shows
				  incorrectly then be sure to report it.

				""");

		save();

		read();

		color_minecraft = Text.color(getString("Extraction.selection.color.minecraft"));
		color_custom = Text.color(getString("Extraction.selection.color.custom"));
		color_curse = Text.color(getString("Extraction.selection.color.curse"));
	}

	private void put(String path, Object value) {
		file.addDefault(path, value);
		keys.add(path);
	}

	private void read() {
		keys.forEach(key -> {
			List<Content> list;
			if(file.isString(key)) {
				String s = file.getString(key);
				if(s == null || s.isEmpty()) list = of();
				else list = of(ContentParser.parse(s));
			} else list = ContentParser.parse(file.getStringList(key));
			if(list == null || list.isEmpty()) return; 
			text.put(key, list);
		});
		keys.clear();
	}

	private static List<Content> of(Content... cs) {
		return cs == null ? new ArrayList<>()
				: Stream.of(cs).collect(Collectors.toList());
	}

	public List<Content> get(String key) {
		return text.get(key);
	}

	private void convert() {
		File old_file = new File(ExtractableEnchantments.instance().getDataFolder(), "lang.yml");
		if(!old_file.exists()) return;

		FileConfiguration old = YamlConfiguration.loadConfiguration(old_file);

		Mover m = new Mover(this, old);

		m.move("Cost.ExperiencePoints.NotEnough", "Price.experience-points.not-enough");
		m.move("Cost.ExperiencePoints.Amount", "Price.experience-points.value", Mover.price);
		m.move("Cost.ExperienceLevels.NotEnough", "Price.experience-levels.not-enough");
		m.move("Cost.ExperienceLevels.Amount", "Price.experience-levels.value", Mover.price);
		m.move("Cost.Material.NotEnough", "Price.materials.not-enough");
		m.move("Cost.Material.Amount", "Price.materials.value", Mover.price.andThen(Mover.material));
		m.move("Cost.Economy.NotEnough", "Price.economy.not-enough");
		m.move("Cost.Economy.Amount", "Price.economy.value", Mover.price);

		m.move("Permission.Warn.Craft", "Permission.warning.craft-extractor");
		m.move("Permission.Warn.Craft", "Permission.warning.craft-dust");
		m.move("Permission.Warn.Use", "Permission.warning.use-extractor");
		m.move("Permission.Warn.Apply", "Permission.warning.apply-book");
		m.move("Permission.Warn.Dust", "Permission.warning.use-dust");
		m.move("Permission.Warn.Split", "Permission.warning.split-dust");

		m.move("Extractor.Lore.Chance", "Extractor.info.chance");
		m.move("Extractor.Lore.Cost", "Extractor.info.price", Mover.price);
		m.move("Extractor.Lore.Destroy", "Extractor.info.destroy");

		m.move("Extraction.Succeed", "Extraction.success", Mover.enchantment);
		m.move("Extraction.Fail", "Extraction.fail");
		m.move("Extraction.Unsafe", "Extraction.unsafe");
		m.move("Extraction.Constraint", "Extraction.constraint");
		m.move("Extraction.Extract.Minecraft", "Extraction.filter.minecraft");
		m.move("Extraction.Extract.Custom", "Extraction.filter.custom");
		m.move("Extraction.Destroy", "Extraction.destroy", Mover.enchantment);

		m.move("Extration.Selection.Name", "Extraction.selection.name");
		m.move("Extration.Selection.Enchantment.Name", "Extraction.selection.enchantment.name", Mover.enchantment);
		m.move("Extration.Selection.Enchantment.Info", "Extraction.selection.enchantment.info");
		m.move("Enchantment.Color.Normal", "Extraction.selection.color.minecraft");
		m.move("Enchantment.Color.Normal", "Extraction.selection.color.custom");
		m.move("Enchantment.Color.Curse", "Extraction.selection.color.curse");

		m.move("Dust.Split.Held", "Dust.split.held");
		m.move("Dust.Split.Amount", "Dust.split.invalid");
		m.move("Dust.Split.Less", "Dust.split.too-large", Mover.value);
		m.move("Dust.Split.Small", "Dust.split.too-small");

		EnchantmentRegistry.ENCHANTMENTS.forEach((key, enchantment) -> {
			m.move("Enchantments." + key.toUpperCase(), "Enchantments." + key);
		});

		m.layout("Item.Layout", "Items.extractor-layout");

		old_file.delete();
	}

	private record Mover(IFile file, FileConfiguration old) {
		static final UnaryOperator<String> price = s -> s.replace("%amount%", "%price%").replace("%cost%", "%price%");
		static final UnaryOperator<String> material = s -> s.replace("%material_name%", "%material%");
		static final UnaryOperator<String> enchantment = s -> s.replace("%enchant%", "%enchantment%");
		static final UnaryOperator<String> value = s -> s.replace("%limit%", "%value%");
		void move(String from, String to) {
			move(from, to, UnaryOperator.identity());
		}
		void move(String from, String to, Function<String, String> replacer) {
			Object o = old.get(from);
			if(o instanceof String s) file.hold(to, Text.fromLegacy(replacer.apply(s)));
			else if(o instanceof List) {
				file.hold(to, old.getStringList(from)
						.stream()
						.map(replacer)
						.map(Text::fromLegacy)
						.toList());
			}
		}
		void layout(String from, String to) {
			List<String> list = old.getStringList(from);
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).equalsIgnoreCase("EMPTY"))
					list.set(i, "!");
				else if(list.get(i).equalsIgnoreCase("COST"))
					list.set(i, "PRICE");
			}
			CF.s.hold(to, list);
		}
	}

}
