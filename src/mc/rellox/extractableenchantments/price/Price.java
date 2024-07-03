package mc.rellox.extractableenchantments.price;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.price.IPrice;
import mc.rellox.extractableenchantments.api.price.PriceType;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.hook.HookRegistry;
import mc.rellox.extractableenchantments.hook.ICurrency;
import mc.rellox.extractableenchantments.text.content.Content;
import mc.rellox.extractableenchantments.utility.Utility;

public abstract class Price implements IPrice {
	
	protected final PriceType type;
	protected final int value;
	
	public Price(PriceType type, int value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public PriceType type() {
		return type;
	}

	@Override
	public int value() {
		return value;
	}

	@Override
	public Content insufficient() {
		return Language.get("Price." + type.key() + ".not-enough");
	}

	@Override
	public Content text() {
		return Language.get("Price." + type.key() + ".value", "price", value);
	}
	
	public static class PricePoints extends Price {

		public PricePoints(int value) {
			super(PriceType.EXPERIENCE_POINTS, value);
		}

		@Override
		public boolean has(Player player) {
			return getXP(player) >= value;
		}

		@Override
		public void remove(Player player) {
			player.giveExp(-value);
		}

		@Override
		public int balance(Player player) {
			return getXP(player);
		}

		private static int getXP(Player player) {
			int level = player.getLevel(), to = getXPToLevelUp(level);
			return getXPAtLevel(level) + Math.round(to * player.getExp());
		}

		private static int getXPAtLevel(int level) {
			int lq = level * level;
			if(level <= 16) return (int) (lq + 6 * level);
			else if(level <= 31) return (int) (2.5 * lq - 40.5 * level + 360.0);
			else return (int) (4.5 * lq - 162.5 * level + 2220.0);
		}

		private static int getXPToLevelUp(int level) {
			if(level <= 15) return 2 * level + 7;
			else if(level <= 30) return 5 * level - 38;
			else return 9 * level - 158;
		}
		
	}
	
	public static class PriceLevels extends Price {

		public PriceLevels(int value) {
			super(PriceType.EXPERIENCE_LEVELS, value);
		}

		@Override
		public boolean has(Player player) {
			return player.getLevel() >= value;
		}

		@Override
		public void remove(Player player) {
			player.setLevel(player.getLevel() - value);
		}

		@Override
		public int balance(Player player) {
			return player.getLevel();
		}
		
	}
	
	public static class PriceMaterials extends Price {
		
		private final ItemStack item;

		public PriceMaterials(int value, Material material) {
			super(PriceType.MATERIALS, value);
			this.item = new ItemStack(material);
		}
		
		@Override
		public Content text() {
			return Language.get("Price." + type.key() + ".value", "price", value, "material", Utility.displayName(item));
		}

		@Override
		public boolean has(Player player) {
			return balance(player) >= value;
		}

		@Override
		public void remove(Player player) {
			ItemStack clone = item.clone();
			clone.setAmount(value);
			player.getInventory().removeItem(clone);
		}

		@Override
		public int balance(Player player) {
			Inventory v = player.getInventory();
			ItemStack slot;
			int h = 0;
			for(int i = 0; i < 36; i++) {
				if((slot = v.getItem(i)) == null) continue;
				if(slot.isSimilar(item) == false) continue;
				h += slot.getAmount();
			}
			return h;
		}
		
	}
	
	public static class PriceEconomy extends Price {
		
		private final ICurrency currency;

		public PriceEconomy(int value) {
			super(PriceType.ECONOMY, value);
			this.currency = HookRegistry.economy.currency();
		}

		@Override
		public boolean has(Player player) {
			return currency.get(player) >= value;
		}

		@Override
		public void remove(Player player) {
			currency.remove(player, value);
		}

		@Override
		public int balance(Player player) {
			return (int) currency.get(player);
		}
		
	}

}
