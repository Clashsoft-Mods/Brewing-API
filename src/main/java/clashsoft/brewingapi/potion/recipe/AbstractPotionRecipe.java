package clashsoft.brewingapi.potion.recipe;

import net.minecraft.item.ItemStack;

public abstract class AbstractPotionRecipe implements IPotionRecipe
{
	protected ItemStack input;
	
	public AbstractPotionRecipe(ItemStack input)
	{
		this.input = input;
	}
	
	@Override
	public IPotionRecipe register()
	{
		PotionRecipes.recipes.add(this);
		return this;
	}
	
	@Override
	public ItemStack getInput()
	{
		return this.input;
	}
}
