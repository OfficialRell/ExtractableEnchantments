package mc.rellox.extractableenchantments.api.item.recipe;

import java.util.List;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import mc.rellox.extractableenchantments.utility.reflect.Reflect.RF;

public interface IRecipe {
	
	static IRecipe empty = new IRecipe() {
		@Override
		public boolean enabled() {
			return false;
		}
		@Override
		public List<RecipeItem> ingredients() {
			return List.of();
		}
		@Override
		public int matching(ItemStack[] matrix) {
			return 0;
		}
		@Override
		public void reduce(ItemStack[] matrix, int amount) {}
		@Override
		public Recipe recipe() {
			return null;
		}
		@Override
		public void update() {}
	};
	
	static RecipeItem of(String item) {
		try {
			if(item.equalsIgnoreCase("empty")) return null;
			if(item.indexOf(':') < 0)
				return new RecipeItem(RF.enumerate(Material.class, item), 1);
			String[] ss = item.split(":");
			return new RecipeItem(RF.enumerate(Material.class, ss[0]), Integer.parseInt(ss[1]));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @return If the recipe is enabled
	 */
	
	boolean enabled();
	
	/**
	 * @return The ingredients of the recipe
	 */
	
	List<RecipeItem> ingredients();
	
	/**
	 * @param matrix - crafting matrix
	 * @return How many items can be crafted with the given matrix
	 */
	
	int matching(ItemStack[] matrix);
	
	/**
	 * Reduces the given amount of ingredients from the crafting matrix.
	 * 
	 * @param matrix - crafting matrix
	 * @param amount - amount to reduce
	 */
	
	void reduce(ItemStack[] matrix, int amount);
	
	/**
	 * @return Bukkit recipe
	 */
	
	Recipe recipe();
	
	/**
	 * Updates the internal Bukkit recipe.
	 */
	
	void update();
	
	/**
	 * @return Namespace key of the recipe, can be {@code null}
	 */
	
	default NamespacedKey namespace() {
		return recipe() instanceof Keyed keyed ? keyed.getKey() : null;
	}
	
	record RecipeItem(Material material, int value) {}

}
