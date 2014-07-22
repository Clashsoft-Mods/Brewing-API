package clashsoft.brewingapi.potion.recipe;

import net.minecraft.item.ItemStack;

public interface IPotionRecipe
{
	public IPotionRecipe register();
	
	/**
	 * Gets the input stack of this {@link IPotionRecipe}.
	 * 
	 * @return the input stack
	 */
	public ItemStack getInput();
	
	public boolean canApply(ItemStack potion);
	
	/**
	 * Applies this {@link IPotionRecipe} to the given {@link ItemStack}
	 * {@code potion}.
	 * 
	 * @param potion
	 *            the potion stack
	 * @return the potion stack
	 */
	public ItemStack apply(ItemStack potion);
}
