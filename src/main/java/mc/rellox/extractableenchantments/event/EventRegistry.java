package mc.rellox.extractableenchantments.event;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.api.dust.IDust;
import mc.rellox.extractableenchantments.api.extractor.IExtractPrice;
import mc.rellox.extractableenchantments.api.extractor.IExtractor;
import mc.rellox.extractableenchantments.api.extractor.extract.ExtractType;
import mc.rellox.extractableenchantments.api.item.IDustItem;
import mc.rellox.extractableenchantments.api.item.IExtractorItem;
import mc.rellox.extractableenchantments.api.item.enchantment.ILevelledEnchantment;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipe;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipeObject;
import mc.rellox.extractableenchantments.api.price.IPrice;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.configuration.Settings;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.extractor.ExtractorRegistry;
import mc.rellox.extractableenchantments.extractor.selection.SelectionExtract;
import mc.rellox.extractableenchantments.item.ItemRegistry;
import mc.rellox.extractableenchantments.item.enchantment.EnchantmentRegistry;
import mc.rellox.extractableenchantments.item.enchantment.LevelledEnchantment;
import mc.rellox.extractableenchantments.utility.Utility;
import mc.rellox.extractableenchantments.utility.Version;
import mc.rellox.extractableenchantments.utility.Version.VersionType;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public final class EventRegistry implements Listener {
	
	public static void initialize() {
		Bukkit.getPluginManager().registerEvents(new EventRegistry(), ExtractableEnchantments.instance());
	}
	
	@EventHandler()
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.discoverRecipes(ExtractorRegistry.all()
				.stream()
				.map(IExtractor::recipe)
				.filter(IRecipe::enabled)
				.map(IRecipe::namespace)
				.collect(Collectors.toList()));
		player.discoverRecipes(DustRegistry.all()
				.stream()
				.map(IDust::recipe)
				.filter(IRecipe::enabled)
				.map(IRecipe::namespace)
				.collect(Collectors.toList()));
	}
	
	@EventHandler
	private void onUseExtractor(InventoryClickEvent event) {
		Inventory clicked = event.getClickedInventory();
		if(clicked == null || clicked.getType() != InventoryType.PLAYER) return;
		if(event.isLeftClick() == false && event.isRightClick() == false) return;
		
		ItemStack item_enchanted = event.getCurrentItem(), item_extractor = event.getCursor();
		if(item_extractor == null || item_extractor.hasItemMeta() == false
				|| item_enchanted == null || item_enchanted.hasItemMeta() == false) return;
		
		if(item_extractor.getAmount() > 1) return;
		
		IExtractor extractor = ExtractorRegistry.get(item_extractor);
		if(extractor == null) return;
		
		ItemMeta meta = item_enchanted.getItemMeta();
		if(meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) == true
				&& extractor.extract().hidden() == false) return;
		
		Player player = (Player) event.getWhoClicked();
		
		List<ILevelledEnchantment> enchantments = EnchantmentRegistry.enchantments(extractor, player, item_enchanted);
		
		if(player.hasPermission("ee.use." + extractor.key()) == false) {
			Language.get("Permission.warning.use-extractor").send(player);
			Settings.settings.sound_warning.play(player);
			return;
		}
		
		if(enchantments.isEmpty() == true || 
				(enchantments.size() == 1 && item_enchanted.getType() == Material.ENCHANTED_BOOK)) {
			return;
		}
		
		event.setCancelled(true);
		
		IExtractPrice extract_price = extractor.price();
		if(extract_price.enabled() == true) {
			IPrice price = extract_price.price();
			if(price.has(player) == false) {
				price.insufficient().send(player);
				Settings.settings.sound_warning.play(player);
				return;
			}
		}
		if(extractor.ignored(item_enchanted) == true) {
			Language.get("Extraction.constraint").send(player);
			Settings.settings.sound_warning.play(player);
			return;
		}
		
		if(extractor.extract().type() == ExtractType.RANDOM) {
			int r = Utility.random(enchantments.size());
			ILevelledEnchantment to_remove = enchantments.get(r);
			
			if(extract_price.enabled() == true) extract_price.price().remove(player);
			
			ExtractorRegistry.extract(extractor, player, item_enchanted, item_extractor, to_remove);
		} else {
			new SelectionExtract(extractor, player, item_extractor, item_enchanted, enchantments);
		}
	}
	
	private static final String prefix_extractor = "extractor_";
	private static final String prefix_dust = "dust_";
	
	@EventHandler(priority = EventPriority.HIGH)
	private void onCraftPrepare(PrepareItemCraftEvent event) {
		prepare(event, prefix_extractor, ExtractorRegistry::get);
		prepare(event, prefix_dust, DustRegistry::get);
	}
	
	private static void prepare(PrepareItemCraftEvent event, String prefix,
			Function<String, IRecipeObject> f) {
		if(!(event.getRecipe() instanceof ShapedRecipe recipe)) return;
		String key = recipe.getKey().getKey();
		
		if(key.startsWith(prefix) == false) return;
		String id = key.substring(prefix.length());

		IRecipeObject object = f.apply(id);
		if(object == null) return;

		CraftingInventory v = event.getInventory();
		ItemStack[] matrix = v.getMatrix();
		if(object.recipe().matching(matrix) <= 0) v.setResult(null);
		else v.setResult(object.result());
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onCraftExtractor(CraftItemEvent event) {
		if(!(event.getRecipe() instanceof Keyed keyed)) return;
		String key = keyed.getKey().getKey();
		
		if(key.startsWith(prefix_extractor) == false) return;
		String id = key.substring(prefix_extractor.length());

		IExtractor extractor = ExtractorRegistry.get(id);
		if(extractor == null) return;
		
		event.setCancelled(true);
		
		CraftingInventory v = event.getInventory();
		ItemStack[] matrix = v.getMatrix();
		if(ExtractorRegistry.contains(matrix) == true
				|| DustRegistry.contains(matrix) == true) {
			v.setResult(null);
			return;
		}

		Player player = (Player) event.getWhoClicked();
		if(player.hasPermission("ee.craft." + extractor.key()) == false) {
			Language.get("Permission.warning.craft-extractor").send(player);
			Settings.settings.sound_warning.play(player);
			v.setResult(null);
			return;
		}
		
		IRecipe recipe = extractor.recipe();
		
		int matching = recipe.matching(matrix);
		if(matching <= 0) {
			v.setResult(null);
			return;
		}
		
		IExtractorItem item = extractor.item();
		
		if(event.isShiftClick() == false) {
			if(event.getClick() == ClickType.NUMBER_KEY) {
				int button = event.getHotbarButton();
				PlayerInventory pi = player.getInventory();
				
				if(ItemRegistry.nulled(pi.getItem(button)) == false) return;
				pi.setItem(button, item.item());
				recipe.reduce(matrix, 1);
			} else {
				ItemStack cursor = player.getItemOnCursor();
				if(extractor.chance().enabled() == true) {
					if(ItemRegistry.nulled(cursor) == false) return;
					player.setItemOnCursor(item.item());
					recipe.reduce(matrix, 1);
				} else if(ItemRegistry.nulled(cursor) == true) {
					player.setItemOnCursor(item.item());
					recipe.reduce(matrix, 1);
				} else return;
			}
		} else {
			int free = ItemRegistry.free(player);
			if(free <= 0) return;
			if(free >= matching) {
				player.getInventory().addItem(item.items(matching));
				recipe.reduce(matrix, matching);
			} else {
				player.getInventory().addItem(item.items(free));
				recipe.reduce(matrix, free);
			}
		}
		v.setMatrix(matrix);
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onCraftDust(CraftItemEvent event) {
		if(!(event.getRecipe() instanceof Keyed keyed)) return;
		String key = keyed.getKey().getKey();
		
		if(key.startsWith(prefix_dust) == false) return;
		String id = key.substring(prefix_dust.length());

		IDust dust = DustRegistry.get(id);
		if(dust == null) return;
		
		event.setCancelled(true);
		
		CraftingInventory v = event.getInventory();
		ItemStack[] matrix = v.getMatrix();
		if(ExtractorRegistry.contains(matrix) == true
				|| DustRegistry.contains(matrix) == true) {
			v.setResult(null);
			return;
		}

		Player player = (Player) event.getWhoClicked();
		if(player.hasPermission("ee.dust.craft." + dust.key()) == false) {
			Language.get("Permission.warning.craft-dust").send(player);
			Settings.settings.sound_warning.play(player);
			v.setResult(null);
			return;
		}
		
		IRecipe recipe = dust.recipe();
		
		int matching = recipe.matching(matrix);
		if(matching <= 0) {
			v.setResult(null);
			return;
		}
		
		IDustItem item = dust.item();
		
		if(event.isShiftClick() == false) {
			if(event.getClick() == ClickType.NUMBER_KEY) {
				int button = event.getHotbarButton();
				PlayerInventory pi = player.getInventory();
				if(ItemRegistry.nulled(pi.getItem(button)) == false) return; 
				pi.setItem(button, item.item(dust.percent()));
				recipe.reduce(matrix, 1);
			} else {
				ItemStack curs = player.getItemOnCursor();
				if(ItemRegistry.nulled(curs) == false) return; 
				player.setItemOnCursor(item.item(dust.percent()));
				recipe.reduce(matrix, 1);
			}
		} else {
			int free = ItemRegistry.free(player);
			if(free <= 0) return; 
			if(free >= matching) {
				player.getInventory().addItem(item.items(dust.percent(), matching));
				recipe.reduce(matrix, matching);
			} else {
				player.getInventory().addItem(item.items(dust.percent(), free));
				recipe.reduce(matrix, free);
			}
		}
		v.setMatrix(matrix);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onApply(InventoryClickEvent event) {
		if(event.getClickedInventory() == null
				|| event.getInventory().getType() != InventoryType.CRAFTING) return;
		ItemStack current = event.getCurrentItem(), cursor = event.getCursor();
		
		Player player = (Player) event.getWhoClicked();
		if(ItemRegistry.nulled(current) == false)  {
			if(ItemRegistry.nulled(cursor) == true) {
				// dust splitting to cursor
				if(event.getClick() != ClickType.SHIFT_RIGHT) return;
				
				ItemStack item_dust = current;
				IDust dust = DustRegistry.get(item_dust);
				if(dust == null || allow(player) == false) return;
				event.setCancelled(true);
				
				if(player.hasPermission("ee.dust.split." + dust.key()) == false) {
					Language.get("Permission.warning.split-dust").send(player);
					Settings.settings.sound_warning.play(player);
					return;
				}
				
				int perc_dust = DustRegistry.readPercent(item_dust);
				
				int amount = item_dust.getAmount();
				if(amount > 1) {
					perc_dust *= amount;
					item_dust.setAmount(1);
					DustRegistry.writePercent(item_dust, perc_dust);
				}
				
				if(perc_dust <= 1) return;
				int half = ((perc_dust - 1) >> 1) + 1;
				perc_dust -= half;
				DustRegistry.writePercent(item_dust, perc_dust);
				dust.item().update(item_dust);
				
				ItemStack item_hand = dust.item().item(half);
				player.setItemOnCursor(item_hand);
				Settings.settings.sound_dust_split.play(player);
				return;
			}
			IDust dust = DustRegistry.get(cursor);
			if(dust == null || allow(player) == false) return;
			IDust other = DustRegistry.get(current);
			if(other != null) {
				// dust combining
				if(dust.equals(other) == false) return;
				event.setCancelled(true);
				
				if(player.hasPermission("ee.dust.use." + dust.key()) == false) {
					Language.get("Permission.warning.use-dust").send(player);
					Settings.settings.sound_warning.play(player);
					return;
				}
				ItemStack item_dust = cursor, item_other = current;
				int perc_dust = DustRegistry.readPercent(item_dust);
				if(perc_dust < 0) return;
				
				int amount = item_dust.getAmount();
				if(amount > 1) {
					perc_dust *= amount;
					item_dust.setAmount(1);
					DustRegistry.writePercent(item_dust, perc_dust);
				}
				
				int perc_other = DustRegistry.readPercent(item_other);
				if(perc_dust < 0) return;
				
				amount = item_other.getAmount();
				if(amount > 1) {
					perc_other *= amount;
					item_other.setAmount(1);
					DustRegistry.writePercent(item_other, perc_other);
				}
				
				int can = other.limit() - perc_other;
				if(can <= 0) return;
				
				if(perc_dust > can) {
					perc_other = other.limit();
					perc_dust -= can;
					DustRegistry.writePercent(item_dust, perc_dust);
					dust.item().update(item_dust);
					DustRegistry.writePercent(item_other, perc_other);
					other.item().update(item_other);
				} else {
					perc_other += perc_dust;
					DustRegistry.writePercent(item_other, perc_other);
					other.item().update(item_other);
					player.setItemOnCursor(null);
				}
				Settings.settings.sound_dust_combine.play(player);
				return;
			}
			// dust applying to extractor
			ItemStack item_dust = cursor, item_hand = current;
			byte b = 0;
			IExtractor extractor = ExtractorRegistry.get(item_hand);
			if(extractor != null) b |= (dust.applicable().accepts(extractor) == true ? 1 : 0);
			else if(dust.applicable().books() == true) b |= (item_hand.getType() == Material.ENCHANTED_BOOK ? 2 : 0);
			
			if(b == 0) return;
			if(player.hasPermission("ee.dust.use." + dust.key()) == false) {
				Language.get("Permission.warning.use-dust").send(player);
				Settings.settings.sound_warning.play(player);
				return;
			}
			int perc_hand = DustRegistry.readChance(item_hand);
			if(perc_hand < 0) return;
			
			event.setCancelled(true);
			
			if(perc_hand >= 100) {
				Settings.settings.sound_warning.play(player);
				return;
			}
			int perc_dust = DustRegistry.readPercent(item_dust);
			
			int amount = item_dust.getAmount();
			if(amount > 1) {
				perc_dust *= amount;
				item_dust.setAmount(1);
				DustRegistry.writePercent(item_dust, perc_dust);
			}
			
			int remove = 100 - perc_hand;
			if(perc_dust > remove) {
				perc_hand = 100;
				perc_dust -= remove;
				DustRegistry.writeChance(item_hand, perc_hand);
				DustRegistry.writePercent(item_dust, perc_dust);
				dust.item().update(item_dust);
			} else {
				perc_hand += perc_dust;
				DustRegistry.writeChance(item_hand, perc_hand);
				player.setItemOnCursor(null);
			}
			Settings.settings.sound_dust_apply.play(player);
			
			if(b == 1) ItemRegistry.replace(item_hand, Language.list("Extractor.info.chance", "chance", perc_hand));
			else ItemRegistry.replace(item_hand, Language.list("Book.info.chance", "chance", perc_hand));
			
		} else if(ItemRegistry.nulled(cursor) == false) {
			// dust splitting from cursor
			if(event.getClick() != ClickType.SHIFT_RIGHT) return;
			
			ItemStack item_dust = cursor;
			IDust dust = DustRegistry.get(item_dust);
			if(dust == null || allow(player) == false) return;
			
			event.setCancelled(true);
			
			if(player.hasPermission("ee.dust.split." + dust.key()) == false) {
				Language.get("Permission.warning.split-dust").send(player);
				Settings.settings.sound_warning.play(player);
				return;
			}
			int perc_dust = DustRegistry.readPercent(item_dust);
			
			int amount = item_dust.getAmount();
			if(amount > 1) {
				perc_dust *= amount;
				item_dust.setAmount(1);
				DustRegistry.writePercent(item_dust, perc_dust);
			}
			
			if(perc_dust <= 1) return;
			
			int half = ((perc_dust - 1) >> 1) + 1;
			perc_dust -= half;
			
			DustRegistry.writePercent(item_dust, perc_dust);
			dust.item().update(item_dust);
			
			ItemStack item_current = dust.item().item(half);
			event.setCurrentItem(item_current);
			
			Settings.settings.sound_dust_split.play(player);
			return;
		}
	}
	
	private static boolean allow(Player player) {
		if(player.getGameMode() != GameMode.CREATIVE) return true;
		player.sendMessage(ChatColor.DARK_RED + "Cannot use dust while in creative mode, "
				+ "due to item duplication!");
		Settings.settings.sound_warning.play(player);
		return false;
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onAnvilUse(InventoryClickEvent event) {
		if(!(event.getInventory() instanceof AnvilInventory anvil)) return;
		if(anvil.equals(event.getClickedInventory()) == false) return;
		if(event.getSlot() != 2) return;
		
		ItemStack item = anvil.getItem(0), book = anvil.getItem(1);
		
		if(ItemRegistry.nulled(item) == true
				|| ItemRegistry.nulled(book) == true) return;
		if(book.getType() != Material.ENCHANTED_BOOK) return;
		
		Player player = (Player) event.getWhoClicked();
		if(player.hasPermission("ee.books.apply") == false) {
			Language.get("Permission.warning.apply-book").send(player);
			Settings.settings.sound_warning.play(player);
			return;
		}
		if(Settings.settings.book_chance_enabled == false
				|| ItemRegistry.chance(book) == true) return;
		
		int level = player.getLevel();
		int cost = repair(player, anvil);
		if(cost > level) return;
		player.setLevel(level - cost);

		Language.get("Book.apply.fail").send(player);
		Settings.settings.sound_book_fail.play(player);
		
		event.setCancelled(true);
		anvil.setItem(1, null);
		
		player.spawnParticle(RF.enumerate(Particle.class, "SMOKE_NORMAL", "SMOKE"),
				player.getLocation().add(0, 1, 0), 25, 0.1, 0.2, 0.1, 0.075);
	}
	
	private int repair(Player player, Inventory v) {
		Object from;
		if(Version.version.atleast(VersionType.v_21_1) == true) from = player.getOpenInventory();
		else from = v;
		return RF.order(from, "getRepairCost").as(int.class).invoke(0);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void onAnvilPrepare(PrepareAnvilEvent event) {
		ItemStack result = event.getResult();
		if(ItemRegistry.nulled(result) == true) return;
		
		AnvilInventory anvil = event.getInventory();
		
		ItemStack first = anvil.getItem(0), second = anvil.getItem(1);
		if(ItemRegistry.nulled(first) == true
				|| ItemRegistry.nulled(second) == true) return;
		
		if(Settings.settings.anvils_apply_books == false) {
			if(second.getType() == Material.ENCHANTED_BOOK) {
				event.setResult(null);
				return;
			}
		}
		if(Settings.restricted(second.getType(), first.getType()) == true) {
			event.setResult(null);
			return;
		}
		if(Settings.settings.anvils_apply_unsafe == true) {
			Map<String, ILevelledEnchantment> enchantments = EnchantmentRegistry.keyed(result);
			if(enchantments.isEmpty() == true) return;
			
			merge(enchantments, EnchantmentRegistry.keyed(first));
			merge(enchantments, EnchantmentRegistry.keyed(second));
			
			enchantments.values().forEach(i -> i.apply(result));
			
			event.setResult(result);
		}
	}
	
	private static void merge(Map<String, ILevelledEnchantment> enchantments,
			Map<String, ILevelledEnchantment> other) {
		other.forEach((key, i) -> {
			ILevelledEnchantment e = enchantments.get(key);
			if(e == null || i.level() <= e.level()) return;
			enchantments.put(key, new LevelledEnchantment(e.enchantment(), i.level()));
		});
	}

}
