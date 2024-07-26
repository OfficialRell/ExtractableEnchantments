package mc.rellox.extractableenchantments.item.recipe;

import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import mc.rellox.extractableenchantments.ExtractableEnchantments;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipe;
import mc.rellox.extractableenchantments.api.item.recipe.IRecipeObject;
import mc.rellox.extractableenchantments.utility.Utility;

public class ItemRecipe implements IRecipe {
	
	public IRecipeObject object;
	
	private final List<RecipeItem> ingredients;
	
	private Recipe recipe;
	
	public ItemRecipe(List<RecipeItem> ingredients) {
		this.ingredients = ingredients;
	}

	@Override
	public boolean enabled() {
		return true;
	}

	@Override
	public List<RecipeItem> ingredients() {
		return ingredients;
	}

	@Override
	public int matching(ItemStack[] matrix) {
		return IntStream.range(0, matrix.length)
				.filter(i -> matrix[i] != null)
				.map(i -> matrix[i].getAmount() / ingredients.get(i).value())
				.min()
				.orElse(0);
	}
	
	@Override
	public void reduce(ItemStack[] matrix, int amount) {
		IntStream.range(0, matrix.length)
		.filter(i -> matrix[i] != null)
		.forEach(i -> {
			int value = ingredients.get(i).value();
			int left = matrix[i].getAmount() - amount * value;
			if(left > 0) matrix[i].setAmount(left);
			else matrix[i] = null;
		});
	}

	@Override
	public Recipe recipe() {
		return recipe;
	}

	private Recipe recipe0() {
		NamespacedKey key = new NamespacedKey(ExtractableEnchantments.instance(), object.prefix() + object.key());
		ShapedRecipe shaped = new ShapedRecipe(key, object.result());
		shaped.shape("abc", "def", "ghi");
		for(int i = 0; i < ingredients.size(); i++) {
			RecipeItem r = ingredients.get(i);
			if(r != null) shaped.setIngredient((char) ('a' + i), r.material());
		}
		return shaped;
	}

	@Override
	public void update() {
		this.recipe = recipe0();
		Utility.update(this);
	}

}
