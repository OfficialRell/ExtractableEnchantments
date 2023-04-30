package mc.rellox.extractableenchantments.usage;

import org.bukkit.entity.Player;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.extractor.Extractor;
import mc.rellox.extractableenchantments.usage.Cost.CostEconomy;
import mc.rellox.extractableenchantments.usage.Cost.CostExperienceLevels;
import mc.rellox.extractableenchantments.usage.Cost.CostExperiencePoints;
import mc.rellox.extractableenchantments.usage.Cost.CostMaterial;

public abstract class CostType {
	
	public static final CostType EXPERIENCE_POINTS = new CostType("EXPERIENCE_POINTS") {
		@Override
		public Cost cost(Extractor e, int i) {
			return new CostExperiencePoints(e, i);
		}
		@Override
		public CostType next() {
			return EXPERIENCE_LEVELS;
		}
	};
	public static final CostType EXPERIENCE_LEVELS = new CostType("EXPERIENCE_LEVELS") {
		@Override
		public Cost cost(Extractor e, int i) {
			return new CostExperienceLevels(e, i);
		}
		@Override
		public CostType next() {
			return MATERIAL;
		}
	};
	public static final CostType MATERIAL = new CostType("MATERIAL") {
		@Override
		public Cost cost(Extractor e, int i) {
			return new CostMaterial(e, i);
		}
		@Override
		public CostType next() {
			return ECONOMY;
		}
	};
	public static final CostType ECONOMY = new CostType("ECONOMY") {
		@Override
		public Cost cost(Extractor e, int i) {
			return new CostEconomy(e, i);
		}
		@Override
		public CostType next() {
			return EXPERIENCE_POINTS;
		}
	};
	
	public static CostType of(String name) {
		if(name == null) return EXPERIENCE_POINTS;
		String s = name.toUpperCase();
		CostType c = null;
		try {
			c = (CostType) CostType.class.getDeclaredField(s).get(null);
			if(c == ECONOMY && ExtractableEnchantments.ECONOMY == null
					|| ExtractableEnchantments.ECONOMY.get() == null) c = EXPERIENCE_POINTS;
		} catch (Exception e) {}
		return c == null ? EXPERIENCE_POINTS : c;
	}
	
	private String name;
	
	private CostType(String name) {
		this.name = name;
	}
	
	public String name() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return name();
	}
	
	public abstract Cost cost(Extractor e, int i);
	
	public abstract CostType next();
	
	public int balance(Extractor e, Player player) {
		return cost(e, 0).balance(player);
	}

}
