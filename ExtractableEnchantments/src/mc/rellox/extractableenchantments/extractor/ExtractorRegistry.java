package mc.rellox.extractableenchantments.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.configuration.Configuration;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.dust.DustRegistry;
import mc.rellox.extractableenchantments.usage.Cost;
import mc.rellox.extractableenchantments.utils.Utils;

public final class ExtractorRegistry implements Listener {
	
	public static NamespacedKey key_extractor, key_random;
	
	public static final List<Extractor> EXTACTORS = new LinkedList<>();
	
	private static final Set<Enchantment> MINECRAFT_ENCHANTMENTS = new HashSet<>();
	static {
		MINECRAFT_ENCHANTMENTS.addAll(Arrays.asList(
				Enchantment.ARROW_DAMAGE, Enchantment.ARROW_FIRE, Enchantment.ARROW_INFINITE,
				Enchantment.ARROW_KNOCKBACK, Enchantment.BINDING_CURSE, Enchantment.CHANNELING,
				Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ARTHROPODS, Enchantment.DAMAGE_UNDEAD,
				Enchantment.DEPTH_STRIDER, Enchantment.DIG_SPEED, Enchantment.DURABILITY,
				Enchantment.FIRE_ASPECT, Enchantment.FROST_WALKER, Enchantment.IMPALING,
				Enchantment.KNOCKBACK, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.LOOT_BONUS_MOBS,
				Enchantment.LURE, Enchantment.MENDING, Enchantment.OXYGEN,
				Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS,
				Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FIRE,
				Enchantment.PROTECTION_PROJECTILE, Enchantment.RIPTIDE, Enchantment.SILK_TOUCH,
				Enchantment.SWEEPING_EDGE, Enchantment.THORNS,
				Enchantment.VANISHING_CURSE, Enchantment.WATER_WORKER));
		Enchantment e;
		if((e = Language.getByName("MULTISHOT")) != null) MINECRAFT_ENCHANTMENTS.add(e);
		if((e = Language.getByName("PIERCING")) != null) MINECRAFT_ENCHANTMENTS.add(e);
		if((e = Language.getByName("QUICK_CHARGE")) != null) MINECRAFT_ENCHANTMENTS.add(e);
		if((e = Language.getByName("SOUL_SPEED")) != null) MINECRAFT_ENCHANTMENTS.add(e);
	}
	
	public static void initialize() {
		Bukkit.getPluginManager().registerEvents(new ExtractorRegistry(), ExtractableEnchantments.instance());
		key_extractor = new NamespacedKey(ExtractableEnchantments.instance(), "extractor");
		key_random = new NamespacedKey(ExtractableEnchantments.instance(), "random");
		update();
	}
	
	public static void update() {
		EXTACTORS.clear();
		EXTACTORS.addAll(Configuration.extractors());
		if(EXTACTORS.isEmpty() == true) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[EE] " + ChatColor.RED + "Unable to load any extractors, "
					+ "make sure everything is correct in configuration file and reload it!");
		}
	}
	
	public static Extractor extractor(String key) {
		return EXTACTORS.stream()
				.filter(e -> e.key.equalsIgnoreCase(key))
				.findFirst()
				.orElse(null);
	}
	
	public static Extractor extractor(ItemStack item) {
		return EXTACTORS.stream()
				.filter(e -> e.is(item))
				.findFirst()
				.orElse(null);
	}

	@EventHandler()
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.discoverRecipes(EXTACTORS.stream()
				.filter(d -> d.recipe != null)
				.map(d -> d.recipe.getKey())
				.collect(Collectors.toList()));
	}

	@EventHandler
	private void onUse(InventoryClickEvent event) {
		Inventory c = event.getClickedInventory();
		if(c == null || c.getType() != InventoryType.PLAYER) return;
		if(event.isLeftClick() == false && event.isRightClick() == false) return;
		ItemStack curr = event.getCurrentItem(), curs = event.getCursor();
		if(curr == null) return;
		Extractor ex = extractor(curs);
		if(ex == null) return;
		if(curr.hasItemMeta() == false) return;
		ItemMeta meta = curr.getItemMeta();
		if(meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
			if(esm.getStoredEnchants().size() <= 1) return;
		} else if(meta.hasEnchants() == false || meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) == true) return;
		Player player = (Player) event.getWhoClicked();
		if(player.hasPermission("ee.use." + ex.key) == false) {
			player.sendMessage(Language.permission_warn_use());
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(curs.getAmount() > 1) return;
		event.setCancelled(true);
		if(ex.cost_toggle == true) {
			Cost cost = ex.cost();
			if(cost.has(player) == false) {
				player.sendMessage(cost.insufficient());
				player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
				return;
			}
		}
		if(ex.ignore(curr) == true) {
			player.sendMessage(Language.extraction_contraint());
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(ex.extraction == ExtractionType.RANDOM) {
			Map<Enchantment, Integer> map;
			boolean storage;
			if(storage = meta instanceof EnchantmentStorageMeta) map = ((EnchantmentStorageMeta) meta).getStoredEnchants();
			else map = meta.getEnchants();
			Set<Enchantment> set = map.keySet().stream()
					.filter(ex.filter())
					.collect(Collectors.toSet());
			if(set.isEmpty() == true) {
				if(map.isEmpty() == false) {
					if(ex.extract == Extract.MINECRAFT) player.sendMessage(Language.extraction_extract_minecraft());
					else if(ex.extract == Extract.CUSTOM) player.sendMessage(Language.extraction_extract_custom());
				}
				return;
			}
			int level = 0, r = Utils.random(set.size());
			Enchantment removed = null;
			for(Enchantment e : set) {
				if(r-- > 0) continue;
				removed = e;
				level = map.get(e);
				break;
			}
			if(removed == null) return;
			if(ex.extract_unsafe == false && level > removed.getMaxLevel()) {
				player.sendMessage(Language.extraction_unsafe());
				return;
			}
			if(ex.cost_toggle == true) ex.cost().remove(player);
			extract(ex, player, curr, removed, level, !ex.chance(curs), storage);
		} else if(ex.extraction == ExtractionType.SELECTION) {
			Map<Enchantment, Integer> map;
			if(meta instanceof EnchantmentStorageMeta) map = ((EnchantmentStorageMeta) meta).getStoredEnchants();
			else map = meta.getEnchants();
			if(map.keySet().stream().anyMatch(ex.filter()) == false) {
				if(ex.extract == Extract.MINECRAFT) player.sendMessage(Language.extraction_extract_minecraft());
				else if(ex.extract == Extract.CUSTOM) player.sendMessage(Language.extraction_extract_custom());
				return;
			}
			new EnchantmentSelection(ex, player, curs, curr);
		}
	}
	
	protected static void extract(Extractor ex, Player player, ItemStack item, Enchantment removed, int level,
			boolean failed, boolean storage) {
		if(ExtractableEnchantments.EXCELLENT_ENCHANTS.get() != null
				&& ExtractableEnchantments.EXCELLENT_ENCHANTS.isEnchantment(removed) == true) {
			ExtractableEnchantments.EXCELLENT_ENCHANTS.extract(ex, player, item, removed, level, failed, storage);
			return;
		}
		if(ExtractableEnchantments.CUSTOM_ENCHANTS.get() != null
				&& ExtractableEnchantments.CUSTOM_ENCHANTS.isEnchantment(removed) == true) {
			ExtractableEnchantments.CUSTOM_ENCHANTS.extract(ex, player, item, removed, level, failed, storage);
			return;
		}
		if(ExtractableEnchantments.ECO_ENCHANTS.get() != null
				&& ExtractableEnchantments.ECO_ENCHANTS.isEnchantment(removed) == true) {
			ExtractableEnchantments.ECO_ENCHANTS.extract(ex, player, item, removed, level, failed, storage);
			return;
		}
		if(failed == true) {
			if(ex.extraction == ExtractionType.RANDOM) player.setItemOnCursor(null);
			if(ex.chance_destroy == true) {
				if(storage == true) {
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
					meta.removeStoredEnchant(removed);
					item.setItemMeta(meta);
				} else {
					ItemMeta meta = item.getItemMeta();
					meta.removeEnchant(removed);
					item.setItemMeta(meta);
				}
				player.sendMessage(Language.exteaction_destroy(removed, level));
				playOnFail(player, true);
			} else {
				player.sendMessage(Language.extraction_fail(removed));
				playOnFail(player, false);
			}
		} else {
			if(storage == true) {
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
				meta.removeStoredEnchant(removed);
				item.setItemMeta(meta);
			} else {
				ItemMeta meta = item.getItemMeta();
				meta.removeEnchant(removed);
				item.setItemMeta(meta);
			}
			ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
			EnchantmentStorageMeta book_meta = (EnchantmentStorageMeta) book.getItemMeta();
			book_meta.addStoredEnchant(removed, level, true);
			if(Configuration.book_chance_toggle() == true) {
				int chance = ex.book_chance_force == true ? ex.book_chance_value : Configuration.book_chance_random();
				List<String> lore = new ArrayList<>();
				lore.add("");
				lore.add(Language.book_lore_chance(chance));
				book_meta.setLore(lore);
				PersistentDataContainer p = book_meta.getPersistentDataContainer();
				p.set(DustRegistry.key_chance, PersistentDataType.INTEGER, chance);
			}
			book.setItemMeta(book_meta);
			if(ex.extraction == ExtractionType.RANDOM) {
				if(player.getGameMode() == GameMode.CREATIVE) player.getInventory().addItem(book);
				else player.setItemOnCursor(book);
			}
			else {
				if(Utils.slots(player) <= 0) player.getWorld().dropItem(player.getLocation(), book);
				else player.getInventory().addItem(book);
			}
			player.sendMessage(Language.extraction_succeed(removed, level));
			player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2f, 0f);
			player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 0f);
			DustOptions o = new DustOptions(Color.BLACK, 1f);
			player.spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1, 0), 50, 0.2, 0.5, 0.2, 0, o);
		}
	}
	
	public static void playOnFail(Player player, boolean destroy) {
		float v = Configuration.extraction_volume;
		Location eyes = player.getEyeLocation();
		if(destroy == true) player.playSound(eyes, Sound.ENTITY_ENDER_DRAGON_HURT, v, 0.5f);
		player.playSound(eyes, Sound.ENTITY_WITHER_HURT, v, 0.5f);
	}
	
	public static boolean contains(ItemStack[] matrix) {
		for(ItemStack item : matrix) {
			for(Extractor x : EXTACTORS) {
				if(x.is(item) == true) {
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	private void onCraftPrep(PrepareItemCraftEvent event) {
		Recipe r = event.getRecipe();
		if(r instanceof ShapedRecipe sr) {
			Extractor ex = null;
			for(Extractor e : EXTACTORS) {
				if(e.recipe_toggle == false) continue;
				if(e.recipe != null && e.recipe.getKey().equals(sr.getKey()) == true) {
					ex = e;
					break;
				}
			}
			if(ex == null) return;
			event.getInventory().setResult(ex.item_static());
		}
	}

	@EventHandler
	private void onCraft(CraftItemEvent event) {
		Recipe r = event.getRecipe();
		if(r instanceof ShapedRecipe == false) return; 
		Extractor ex = null;
		for(Extractor e : EXTACTORS) {
			if(e.recipe_toggle == false) continue;
			if(e.recipe != null && e.recipe.getKey().equals(((ShapedRecipe) r).getKey()) == true) {
				ex = e;
				break;
			}
		}
		if(ex == null) return;
		event.setCancelled(true);
		CraftingInventory ci = event.getInventory();
		ItemStack[] matrix = ci.getMatrix();
		if(contains(matrix) == true || DustRegistry.contains(matrix) == true) {
			event.getInventory().setResult(null);
			return;
		}
		for(ItemStack item : matrix) {
			for(Extractor x : EXTACTORS) {
				if(x.is(item) == true) {
					event.getInventory().setResult(null);
					return;
				}
			}
		}
		Player player = (Player) event.getWhoClicked();
		if(player.hasPermission("ee.craft." + ex.key) == false) {
			player.sendMessage(Language.permission_warn_craft());
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			event.getInventory().setResult(null);
			return;
		}
		int a = Stream.of(matrix)
				.filter(i -> i != null)
				.mapToInt(ItemStack::getAmount)
				.min().orElse(64);
		if(event.isShiftClick() == false) {
			if(event.getClick() == ClickType.NUMBER_KEY) {
				int button = event.getHotbarButton();
				PlayerInventory pi = player.getInventory();
				if(Utils.isNull(pi.getItem(button)) == false) return; 
				pi.setItem(button, ex.item());
				matrix(matrix, 1);
			} else {
				ItemStack curs = player.getItemOnCursor();
				if(ex.chance_toggle == true) {
					if(Utils.isNull(curs) == false) return; 
					player.setItemOnCursor(ex.item());
					matrix(matrix, 1);
				} else if(Utils.isNull(curs) == true) {
					player.setItemOnCursor(ex.item());
					matrix(matrix, 1);
				} else return;
			}
		} else {
			int f = Utils.slots(player);
			if(f <= 0) return; 
			if(f >= a) {
				player.getInventory().addItem(ex.items(a));
				matrix(matrix, a);
			} else {
				player.getInventory().addItem(ex.items(f));
				matrix(matrix, f);
			}
		}
		ci.setMatrix(matrix);
	}

	private static void matrix(ItemStack[] matrix, int a) {
		for(int i = 0, l; i < matrix.length; i++) {
			ItemStack item = matrix[i];
			if(item != null) {
				if((l = item.getAmount() - a) <= 0) matrix[i] = null;
				else item.setAmount(l);
			}
		}
	}

	@EventHandler
	private void onAnvilUse(InventoryClickEvent event) {
		Inventory ci = event.getClickedInventory();
		if(ci == null) return; 
		Inventory v = event.getInventory();
		if(v instanceof AnvilInventory == false) return; 
		if(ci.equals(v) == false) return; 
		AnvilInventory a = (AnvilInventory) v;
		int s = event.getSlot();
		if(s != 2) return; 
		ItemStack item = a.getItem(2), book = a.getItem(1);
		if(item == null || book == null) return; 
		if(book.getType() != Material.ENCHANTED_BOOK) return;
		Player player = (Player) event.getWhoClicked();
		if(player.hasPermission("ee.books.apply") == false) {
			player.sendMessage(Language.permission_warn_apply());
			player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2f, 1f);
			return;
		}
		if(Configuration.book_chance_toggle() == false || chance(book) == true) return; 
		int l = player.getLevel();
		int o = a.getRepairCost();
		if(o > l) return; 
		l -= o;
		player.setLevel(l);
		player.sendMessage(Language.book_apply_fail());
		player.playSound(player.getEyeLocation(), Sound.ENTITY_WITHER_HURT, 0.85f, 0.5f);
		player.playSound(player.getEyeLocation(), Sound.ENTITY_ELDER_GUARDIAN_DEATH, 2f, 0.5f);
		event.setCancelled(true);
		a.setItem(1, null);
	}


	@EventHandler
	private void onAnvilPrepare(PrepareAnvilEvent event) {
		ItemStack result = event.getResult();
		if(Utils.isNull(result) == true) return;
		AnvilInventory ai = event.getInventory();
		ItemStack first = ai.getItem(1), second = ai.getItem(1);
		if(Utils.isNull(first) == true || Utils.isNull(second) == true) return;
		if(Configuration.anvil_apply_books() == false) {
			if(second.getType() == Material.ENCHANTED_BOOK) {
				event.setResult(null);
				return;
			}
		}
		if(Configuration.restricted(second.getType(), first.getType()) == true) {
			event.setResult(null);
			return;
		}
		if(Configuration.anvil_apply_unsafe() == true) {
			Map<Enchantment, Integer> outcome = enchantments(result);
			ItemMeta meta = result.getItemMeta();
			if(outcome.isEmpty() == true) return;
			Map<Enchantment, Integer> map = new HashMap<>(outcome);
			enchantments(first).forEach((e, l) -> map.merge(e, l, Math::max));
			enchantments(second).forEach((e, l) -> map.merge(e, l, Math::max));
			enchant(meta, map);
			result.setItemMeta(meta);
			event.setResult(result);
		}
	}
	
	private static Map<Enchantment, Integer> enchantments(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if(meta instanceof EnchantmentStorageMeta storage)
			return storage.getStoredEnchants();
		return meta.getEnchants();
	}
	
	private static void enchant(ItemMeta meta, Map<Enchantment, Integer> map) {
		if(meta instanceof EnchantmentStorageMeta storage)
			map.forEach((e, l) -> storage.addStoredEnchant(e, l, true));
		else map.forEach((e, l) -> meta.addEnchant(e, l, true));
	}
	
	private static boolean chance(ItemStack book) {
		ItemMeta meta = book.getItemMeta();
		PersistentDataContainer p = meta.getPersistentDataContainer();
		return Utils.random(100) <= p.getOrDefault(DustRegistry.key_chance, PersistentDataType.INTEGER, 100);
	}
	
	private static Predicate<Enchantment> minecraftFilter() {
		return MINECRAFT_ENCHANTMENTS::contains;
	}

	public static enum ExtractionType {
		RANDOM, SELECTION;
		
		public static ExtractionType of(String name) {
			try {
				return ExtractionType.valueOf(name.toUpperCase());
			} catch (Exception e) {
				return RANDOM;
			}
		}
	}

	public static enum Extract {
		ALL {
			@Override
			public Predicate<Enchantment> filter() {
				return e -> true;
			}
		},
		MINECRAFT {
			@Override
			public Predicate<Enchantment> filter() {
				return minecraftFilter();
			}
		},
		CUSTOM {
			@Override
			public Predicate<Enchantment> filter() {
				return minecraftFilter().negate();
			}
		};
		
		public abstract Predicate<Enchantment> filter();
		
		public static Extract of(String name) {
			try {
				return Extract.valueOf(name.toUpperCase());
			} catch (Exception e) {
				return ALL;
			}
		}
	}
	
	public static enum Constraint {
		
		ITEM_WITH_NAME() {
			@Override
			public boolean ignore(ItemMeta meta) {
				return meta.hasDisplayName() == true;
			}
		},
		ITEM_WITH_LORE() {
			@Override
			public boolean ignore(ItemMeta meta) {
				return meta.hasLore() == true;
			}
		},
		ITEM_WITH_MODEL() {
			@Override
			public boolean ignore(ItemMeta meta) {
				return meta.hasCustomModelData() == true;
			}
		},
		ITEM_WITH_FLAGS() {
			@Override
			public boolean ignore(ItemMeta meta) {
				return Stream.of(ItemFlag.values()).anyMatch(meta::hasItemFlag);
			}
		},
		ITEM_UNBREAKABLE() {
			@Override
			public boolean ignore(ItemMeta meta) {
				return meta.isUnbreakable() == true;
			}
		};
		
		public abstract boolean ignore(ItemMeta meta);

		public static Constraint of(String name) {
			try {
				return Constraint.valueOf(name.toUpperCase());
			} catch (Exception e) {
				return null;
			}
		}
	}
	
}
