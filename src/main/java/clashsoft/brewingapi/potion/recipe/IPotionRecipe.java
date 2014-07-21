package clashsoft.brewingapi.potion.recipe;

import net.minecraft.item.ItemStack;

public interface IPotionRecipe
{
	public ItemStack apply(ItemStack stack);
	
	public ItemStack getInput();
}
