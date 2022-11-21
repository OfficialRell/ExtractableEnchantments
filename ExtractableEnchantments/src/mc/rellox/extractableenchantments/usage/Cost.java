package mc.rellox.extractableenchantments.usage;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.configuration.Language;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.utils.Utils;

public abstract class Cost {
	
	private static boolean isOp(Player player) {
		return player.isOp() && player.getGameMode() == GameMode.CREATIVE;
	}
	
	public final Extractor extractor;
	public final int value;
	
	public Cost(Extractor e, int i) {
		this.extractor = e;
		this.value = i;
	}
	
	public abstract boolean has(Player player);
	
	public abstract String insufficient();
	
	public abstract String cost();
	
	public abstract void remove(Player player);
	
	public abstract String requires(Player player);
	
	public abstract int balance(Player player);
	
	public static class CostExperiencePoints extends Cost {
		
		public CostExperiencePoints(Extractor e, int i) {
			super(e, i);
		}

		@Override
		public boolean has(Player player) {
			if(isOp(player) == true) return true;
			return getXP(player) >= value;
		}
		
		@Override
		public String insufficient() {
			return Language.cost_experience_points_insufficient();
		}
		
		@Override
		public String cost() {
			return Language.cost_experience_points_amount(value);
		}
		
		@Override
		public void remove(Player player) {
			removeXP(player, value);
		}
		
		@Override
		public String requires(Player player) {
			int h = value - getXP(player);
			return Language.cost_experience_points_amount(h);
		}

		@Override
		public int balance(Player player) {
			return getXP(player);
		}
		
	}
	
	public static class CostExperienceLevels extends Cost {
		
		public CostExperienceLevels(Extractor e, int i) {
			super(e, i);
		}

		@Override
		public boolean has(Player player) {
			if(isOp(player) == true) return true;
			return player.getLevel() >= value;
		}
		
		@Override
		public String insufficient() {
			return Language.cost_experience_levels_insufficient();
		}
		
		@Override
		public String cost() {
			return Language.cost_experience_levels_amount(value);
		}
		
		@Override
		public void remove(Player player) {
			player.setLevel(player.getLevel() - value);
		}
		
		@Override
		public String requires(Player player) {
			int h = value - player.getLevel();
			return Language.cost_experience_levels_amount(h);
		}

		@Override
		public int balance(Player player) {
			return player.getLevel();
		}
		
	}
	
	public static class CostMaterial extends Cost {
		
		public CostMaterial(Extractor e, int i) {
			super(e, i);
		}

		@Override
		public boolean has(Player player) {
			if(isOp(player) == true) return true;
			if(extractor.cost_material == null) return false;
			Inventory v = player.getInventory();
			ItemStack slot;
			int h = 0;
			for(int i = 0; i < 36 && h < value; i++) {
				if((slot = v.getItem(i)) == null) continue;
				if(slot.getType() != extractor.cost_material) continue;
				h += slot.getAmount();
			}
			return h >= value;
		}
		
		@Override
		public String insufficient() {
			return Language.cost_material_insufficient();
		}
		
		@Override
		public String cost() {
			return Language.cost_material_amount(value, extractor.cost_material);
		}
		
		@Override
		public void remove(Player player) {
			ItemStack t = new ItemStack(extractor.cost_material);
			t.setAmount(value);
			player.getInventory().removeItem(t);
		}
		
		@Override
		public String requires(Player player) {
			Inventory v = player.getInventory();
			ItemStack slot;
			int h = 0;
			for(int i = 0; i < 36; i++) {
				if((slot = v.getItem(i)) == null) continue;
				if(slot.getType() != extractor.cost_material) continue;
				h += slot.getAmount();
			}
			return Language.cost_material_amount(value - h, extractor.cost_material);
		}

		@Override
		public int balance(Player player) {
			Inventory v = player.getInventory();
			ItemStack slot;
			int h = 0;
			for(int i = 0; i < 36; i++) {
				if((slot = v.getItem(i)) == null) continue;
				if(slot.getType() != extractor.cost_material) continue;
				h += slot.getAmount();
			}
			return h;
		}
		
	}
	
	public static class CostEconomy extends Cost {
		
		public CostEconomy(Extractor e, int i) {
			super(e, i);
		}

		@Override
		public boolean has(Player player) {
			if(isOp(player) == true) return true;
			return ExtractableEnchantments.ECONOMY.get() == null ?
					false : ExtractableEnchantments.ECONOMY.get().has(player, value);
		}
		
		@Override
		public String insufficient() {
			return Language.cost_economy_insufficient();
		}
		
		@Override
		public String cost() {
			double d = value;
			return Language.cost_economy_amount(d);
		}
		
		@Override
		public void remove(Player player) {
			if(ExtractableEnchantments.ECONOMY.get() == null) return;
			ExtractableEnchantments.ECONOMY.get().withdrawPlayer(player, value);
		}
		
		@Override
		public String requires(Player player) {
			double h = ExtractableEnchantments.ECONOMY.get() == null ?
					0 : ExtractableEnchantments.ECONOMY.get().getBalance(player);
			double v = value;
			return Language.cost_economy_amount(Utils.round(v - h));
		}

		@Override
		public int balance(Player player) {
			return (int) (ExtractableEnchantments.ECONOMY.get() == null ?
					0 : ExtractableEnchantments.ECONOMY.get().getBalance(player));
		}
		
	}

	private static boolean removeXP(Player player, int exp) {
		int xp = getXP(player);
		if(xp < exp) return false;
		player.giveExp(-exp);
		return true;
	}

	private static int getXP(Player player) {
		int v = player.getLevel();
		int l = getXPAtLevel(v);
		int u = getXPToLevelUp(v);
		return l + Math.round(u * player.getExp());
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
