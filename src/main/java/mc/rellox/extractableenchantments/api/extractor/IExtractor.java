package mc.rellox.extractableenchantments.api.extractor;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import mc.rellox.extractableenchantments.api.extractor.constraint.IConstraint;
import mc.rellox.extractableenchantments.api.extractor.extract.IExtract;
import mc.rellox.extractableenchantments.api.item.IExtractorItem;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipeObject;

public interface IExtractor extends IRecipeObject {
	
	/**
	 * @return Extractor item
	 */
	
	IExtractorItem item();
	
	/**
	 * Returns the extractor chance. <br>
	 * Will return {@link IExtractorChance#empty} if disabled.
	 * 
	 * @return Extractor chance
	 */
	
	IExtractorChance chance();
	
	/**
	 * @return Should the extracted book be returned
	 */
	
	boolean clearing();
	
	/**
	 * Returns the extractor price. <br>
	 * Will return {@link IExtractPrice#empty} if disabled.
	 * 
	 * @return Extraction price
	 */
	
	IExtractPrice price();
	
	/**
	 * @return Extracted book chance override
	 */
	
	IChanceOverride override();
	
	/**
	 * @return Extractor extraction options
	 */
	
	IExtract extract();
	
	/**
	 * @return Extractor item constraints
	 */
	
	List<IConstraint> constraints();
	
	/**
	 * @return Can this extractor be stacked
	 */
	
	boolean stackable();
	
	/**
	 * @param item - item
	 * @return {@code true} if any constraint ignores the item
	 */
	
	default boolean ignored(ItemStack item) {
		return constraints().stream()
				.anyMatch(c -> c.ignored(item));
	}
	
	@Override
	default ItemStack result() {
		return item().constant();
	}
	
	@Override
	default String prefix() {
		return "extractor_";
	}

}
