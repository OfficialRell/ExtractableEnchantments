package mc.rellox.extractableenchantments.api.extractor.constraint;

import org.bukkit.inventory.ItemStack;

public interface IConstraint {
	
	/**
	 * @return The type of constraint
	 */
	
	ConstraintType type();
	
	/**
	 * @param item - iten
	 * @return If the item is ignored by this constraint
	 */
	
	boolean ignored(ItemStack item);

}
