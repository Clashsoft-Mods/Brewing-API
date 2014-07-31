package clashsoft.brewingapi.potion.recipe;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.cslib.minecraft.stack.CSStacks;

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
	
	@Override
	public boolean canApply(ItemStack ingredient, PotionTypeList potionTypes)
	{
		return CSStacks.itemEquals(this.getInput(), ingredient) && this.canApply(potionTypes);
	}
	
	public abstract boolean canApply(PotionTypeList potionTypes);
	
	@Override
	public void apply(ItemStack ingredient, PotionTypeList potionTypes)
	{
		this.apply(potionTypes);
	}
	
	public abstract void apply(PotionTypeList potionTypes);
}
