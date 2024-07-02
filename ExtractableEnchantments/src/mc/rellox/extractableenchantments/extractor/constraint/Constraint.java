package mc.rellox.extractableenchantments.extractor.constraint;

import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mc.rellox.extractableenchantments.api.extractor.constraint.ConstraintType;
import mc.rellox.extractableenchantments.api.extractor.constraint.IConstraint;
import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public abstract class Constraint implements IConstraint {
	
	public static Constraint of(String input) {
		String[] ss = input.split(":", 2);
		String arg = ss.length < 2 ? null : ss[1];
		try {
			ConstraintType type = RF.enumerate(ConstraintType.class, ss[0]);
			if(type == ConstraintType.ITEM_BY_MATERIAL) {
				Material material = RF.enumerate(Material.class, arg);
				return new ConstraintMaterial(material);
			}
			if(type == ConstraintType.ITEM_WITH_NAME) {
				return new ConstraintName(arg);
			}
			if(type == ConstraintType.ITEM_WITH_LORE) {
				return new ConstraintLore(arg);
			}
			if(type == ConstraintType.ITEM_WITH_MODEL) {
				int model = arg == null ? 0 : Integer.parseInt(arg);
				return new ConstraintModel(model);
			}
			if(type == ConstraintType.ITEM_WITH_FLAG) {
				ItemFlag flag = arg == null
						? null : RF.enumerate(ItemFlag.class, arg);
				return new ConstraintFlag(flag);
			}
			if(type == ConstraintType.ITEM_UNBREAKABLE) {
				return new ConstraintUnbreakable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected ConstraintType type;
	
	public Constraint(ConstraintType type) {
		this.type = type;
	}
	
	@Override
	public ConstraintType type() {
		return type;
	}
	
	private static class ConstraintMaterial extends Constraint {
		
		private final Material material;

		public ConstraintMaterial(Material material) {
			super(ConstraintType.ITEM_BY_MATERIAL);
			this.material = Objects.requireNonNull(material);
		}

		@Override
		public boolean ignored(ItemStack item) {
			return item == null ? true : item.getType() == material;
		}
		
	}
	
	private static class ConstraintName extends Constraint {
		
		private final String name;

		public ConstraintName(String name) {
			super(ConstraintType.ITEM_WITH_NAME);
			this.name = name == null ? null : ChatColor.stripColor(name);
		}

		@Override
		public boolean ignored(ItemStack item) {
			if(item == null || item.hasItemMeta() == false) return true;
			ItemMeta meta = item.getItemMeta();
			if(name == null) return meta.hasDisplayName();
			String display = meta.getDisplayName();
			return ChatColor.stripColor(display).equalsIgnoreCase(name);
		}
		
	}
	
	private static class ConstraintLore extends Constraint {
		
		private final String lore;

		public ConstraintLore(String lore) {
			super(ConstraintType.ITEM_WITH_LORE);
			this.lore = lore == null ? null : ChatColor.stripColor(lore);
		}

		@Override
		public boolean ignored(ItemStack item) {
			if(item == null || item.hasItemMeta() == false) return true;
			ItemMeta meta = item.getItemMeta();
			if(lore == null) return meta.hasLore();
			List<String> lores = meta.getLore();
			return lores.stream()
					.map(ChatColor::stripColor)
					.anyMatch(lore::equalsIgnoreCase);
		}
		
	}
	
	private static class ConstraintModel extends Constraint {
		
		private final int model;

		public ConstraintModel(int model) {
			super(ConstraintType.ITEM_WITH_MODEL);
			this.model = model;
		}

		@Override
		public boolean ignored(ItemStack item) {
			if(item == null || item.hasItemMeta() == false) return true;
			ItemMeta meta = item.getItemMeta();
			if(model == 0) return meta.hasCustomModelData();
			return meta.getCustomModelData() == model;
		}
		
	}
	
	private static class ConstraintFlag extends Constraint {
		
		private final ItemFlag flag;

		public ConstraintFlag(ItemFlag flag) {
			super(ConstraintType.ITEM_WITH_FLAG);
			this.flag = flag;
		}

		@Override
		public boolean ignored(ItemStack item) {
			if(item == null || item.hasItemMeta() == false) return true;
			ItemMeta meta = item.getItemMeta();
			if(flag == null) return meta.getItemFlags().isEmpty() == false;
			return meta.hasItemFlag(flag);
		}
		
	}
	
	private static class ConstraintUnbreakable extends Constraint {

		public ConstraintUnbreakable() {
			super(ConstraintType.ITEM_UNBREAKABLE);
		}

		@Override
		public boolean ignored(ItemStack item) {
			if(item == null || item.hasItemMeta() == false) return true;
			return item.getItemMeta().isUnbreakable();
		}
		
	}

}
