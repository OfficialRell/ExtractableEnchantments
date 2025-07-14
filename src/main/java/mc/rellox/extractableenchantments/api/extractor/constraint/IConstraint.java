package mc.rellox.extractableenchantments.api.extractor.constraint;

import org.bukkit.inventory.ItemStack;

public interface IConstraint {
	
	ConstraintType type();
	
	boolean ignored(ItemStack item);

}
