package mc.rellox.extractableenchantments.extractor;

import org.bukkit.Material;

import mc.rellox.extractableenchantments.utils.Utils;

public enum Restriction {
	
	BOOK_TO_BOOK() {
		@Override
		public boolean restricted(Material what, Material to) {
			return what == Material.ENCHANTED_BOOK && to == what;
		}
	},
	BOOK_TO_ARMOR() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isArmor(to) == true;
		}
	},
	BOOK_TO_WEAPON() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isWeapon(to) == true;
		}
	},
	BOOK_TO_SWORD() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isSword(to) == true;
		}
	},
	BOOK_TO_ARCHERY() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isArchery(to) == true;
		}
	},
	BOOK_TO_TOOL() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isTool(to) == true;
		}
	},
	BOOK_TO_MISC() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isMisc(to) == true;
		}
	},
	ARMOR_TO_ARMOR() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isArmor(what) == true && what == to;
		}
	},
	WEAPON_TO_WEAPON() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isWeapon(what) == true && what == to;
		}
	},
	ARCHERY_TO_ARCHERY() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isArchery(what) == true && what == to;
		}
	},
	SWORD_TO_SWORD() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isSword(what) == true && what == to;
		}
	},
	TOOL_TO_TOOL() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isTool(what) == true && what == to;
		}
	},
	MISC_TO_MISC() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isMisc(what) == true && what == to;
		}
	};
	
	public abstract boolean restricted(Material what, Material to);
	
	public static Restriction of(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (Exception e) {}
		return null;
	}
	
	private static boolean isArmor(Material m) {
		switch(m) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_LEGGINGS:
		case CHAINMAIL_BOOTS:
		case IRON_HELMET:
		case IRON_CHESTPLATE:
		case IRON_LEGGINGS:
		case IRON_BOOTS:
		case GOLDEN_HELMET:
		case GOLDEN_CHESTPLATE:
		case GOLDEN_LEGGINGS:
		case GOLDEN_BOOTS:
		case DIAMOND_HELMET:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_LEGGINGS:
		case DIAMOND_BOOTS:
			return true;
		default:
			Material a;
			if((a = Utils.material("NETHERITE_HELMET")) != null && a == m) return true;
			if((a = Utils.material("NETHERITE_CHESTPLATE")) != null && a == m) return true;
			if((a = Utils.material("NETHERITE_LEGGINGS")) != null && a == m) return true;
			if((a = Utils.material("NETHERITE_BOOTS")) != null && a == m) return true;
			return false;
		}
	}
	
	private static boolean isWeapon(Material m) {
		switch(m) {
		case WOODEN_SWORD:
		case STONE_SWORD:
		case IRON_SWORD:
		case GOLDEN_SWORD:
		case DIAMOND_SWORD:
		case BOW:
		case CROSSBOW:
		case TRIDENT:
			return true;
		default:
			Material a;
			if((a = Utils.material("NETHERITE_SWORD")) != null && a == m) return true;
			return false;
		}
	}
	
	private static boolean isSword(Material m) {
		switch(m) {
		case WOODEN_SWORD:
		case STONE_SWORD:
		case IRON_SWORD:
		case GOLDEN_SWORD:
		case DIAMOND_SWORD:
			return true;
		default:
			Material a;
			if((a = Utils.material("NETHERITE_SWORD")) != null && a == m) return true;
			return false;
		}
	}
	
	private static boolean isArchery(Material m) {
		return m == Material.BOW || m == Material.CROSSBOW;
	}
	
	private static boolean isTool(Material m) {
		switch(m) {
		case WOODEN_PICKAXE:
		case WOODEN_AXE:
		case WOODEN_SHOVEL:
		case WOODEN_HOE:
			
		case STONE_PICKAXE:
		case STONE_AXE:
		case STONE_SHOVEL:
		case STONE_HOE:
			
		case IRON_PICKAXE:
		case IRON_AXE:
		case IRON_SHOVEL:
		case IRON_HOE:
			
		case GOLDEN_PICKAXE:
		case GOLDEN_AXE:
		case GOLDEN_SHOVEL:
		case GOLDEN_HOE:
			
		case DIAMOND_PICKAXE:
		case DIAMOND_AXE:
		case DIAMOND_SHOVEL:
		case DIAMOND_HOE:
			return true;
		default:
			Material a;
			if((a = Utils.material("NETHERITE_PICKAXE")) != null && a == m) return true;
			if((a = Utils.material("NETHERITE_AXE")) != null && a == m) return true;
			if((a = Utils.material("NETHERITE_SHOVEL")) != null && a == m) return true;
			if((a = Utils.material("NETHERITE_HOE")) != null && a == m) return true;
			return false;
		}
	}
	
	private static boolean isMisc(Material m) {
		Material a;
		if((a = Utils.material("WARPED_FUNGUS_ON_A_STICK")) != null && a == m) return true;
		return m == Material.ELYTRA || m == Material.FISHING_ROD || m == Material.FLINT_AND_STEEL || m == Material.SHEARS
				 || m == Material.CARROT_ON_A_STICK;
	}

}
