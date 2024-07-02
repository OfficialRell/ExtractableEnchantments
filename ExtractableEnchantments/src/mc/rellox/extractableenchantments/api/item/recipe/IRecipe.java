package mc.rellox.extractableenchantments.api.item.recipe;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

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
		public CraftingRecipe recipe() {
			return null;
		}
		@Override
		public void update() {}
	};
	
	static RecipeItem of(String item) {
		try {
			if(item.equalsIgnoreCase("empty") == true) return null;
			if(item.indexOf(':') < 0)
				return new RecipeItem(RF.enumerate(Material.class, item), 1);
			String[] ss = item.split(":");
			return new RecipeItem(RF.enumerate(Material.class, ss[0]), Integer.parseInt(ss[1]));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	boolean enabled();
	
	List<RecipeItem> ingredients();
	
	int matching(ItemStack[] matrix);
	
	void reduce(ItemStack[] matrix, int amount);
	
	CraftingRecipe recipe();
	
	void update();
	
	record RecipeItem(Material material, int value) {}

}
