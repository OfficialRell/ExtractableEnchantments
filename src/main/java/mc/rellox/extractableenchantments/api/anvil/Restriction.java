package mc.rellox.extractableenchantments.api.anvil;

import org.bukkit.Material;

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
			return isArmor(to);
		}
	},
	BOOK_TO_WEAPON() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isWeapon(to);
		}
	},
	BOOK_TO_SWORD() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isSword(to);
		}
	},
	BOOK_TO_ARCHERY() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isArchery(to);
		}
	},
	BOOK_TO_TOOL() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isTool(to);
		}
	},
	BOOK_TO_MISC() {
		@Override
		public boolean restricted(Material what, Material to) {
			if(what != Material.ENCHANTED_BOOK) return false;
			return isMisc(to);
		}
	},
	ARMOR_TO_ARMOR() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isArmor(what) && what == to;
		}
	},
	WEAPON_TO_WEAPON() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isWeapon(what) && what == to;
		}
	},
	ARCHERY_TO_ARCHERY() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isArchery(what) && what == to;
		}
	},
	SWORD_TO_SWORD() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isSword(what) && what == to;
		}
	},
	TOOL_TO_TOOL() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isTool(what) && what == to;
		}
	},
	MISC_TO_MISC() {
		@Override
		public boolean restricted(Material what, Material to) {
			return isMisc(what) && what == to;
		}
	};
	
	public abstract boolean restricted(Material what, Material to);
	
	private static boolean isArmor(Material m) {
		return switch(m.name()) {
		case "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS",
		"CHAINMAIL_HELMET","CHAINMAIL_CHESTPLATE", "CHAINMAIL_LEGGINGS", "CHAINMAIL_BOOTS",
		"IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS", "IRON_BOOTS",
		"GOLDEN_HELMET", "GOLDEN_CHESTPLATE", "GOLDEN_LEGGINGS", "GOLDEN_BOOTS",
		"DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS",
		"NETHERITE_HELMET", "NETHERITE_CHESTPLATE", "NETHERITE_LEGGINGS", "NETHERITE_BOOTS"
		-> true;
		default -> false;
		};
	}
	
	private static boolean isWeapon(Material m) {
		return isSword(m)
				|| switch(m.name()) {
				case "BOW", "CROSSBOW", "TRIDENT"
				-> true;
				default -> false;
				};
	}
	
	private static boolean isSword(Material m) {
		return switch(m.name()) {
		case "WOODEN_SWORD", "STONE_SWORD", "IRON_SWORD", "GOLDEN_SWORD",
		"DIAMOND_SWORD", "NETHERITE_SWORD"
		-> true;
		default -> false;
		};
	}
	
	private static boolean isArchery(Material m) {
		return m == Material.BOW || m == Material.CROSSBOW;
	}
	
	private static boolean isTool(Material m) {
		return switch(m.name()) {
		case "WOODEN_PICKAXE", "WOODEN_AXE", "WOODEN_SHOVEL", "WOODEN_HOE",
		"STONE_PICKAXE", "STONE_AXE", "STONE_SHOVEL", "STONE_HOE",
		"IRON_PICKAXE", "IRON_AXE", "IRON_SHOVEL", "IRON_HOE",
		"GOLDEN_PICKAXE", "GOLDEN_AXE", "GOLDEN_SHOVEL", "GOLDEN_HOE",
		"DIAMOND_PICKAXE", "DIAMOND_AXE", "DIAMOND_SHOVEL", "DIAMOND_HOE",
		"NETHERITE_PICKAXE", "NETHERITE_AXE", "NETHERITE_SHOVEL", "NETHERITE_HOE"
		-> true;
		default -> false;
		};
	}

	private static boolean isMisc(Material m) {
		return switch(m.name()) {
		case "WARPED_FUNGUS_ON_A_STICK", "ELYTRA", "FISHING_ROD", "FLINT_AND_STEEL",
		"SHEARS", "CARROT_ON_A_STICK" -> true;
		default -> false;
		};
	}

}
