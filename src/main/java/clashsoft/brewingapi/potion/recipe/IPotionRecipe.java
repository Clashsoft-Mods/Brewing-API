package clashsoft.brewingapi.potion.recipe;

import clashsoft.brewingapi.potion.PotionTypeList;

import net.minecraft.item.ItemStack;

public interface IPotionRecipe
{
	/**
	 * Registers this {@link IPotionRecipe}. It is recommended to register it by
	 * adding it to {@link PotionRecipes#recipes}. See
	 * {@link AbstractPotionRecipe#register()} for a template.
	 * 
	 * @see AbstractPotionRecipe#register()
	 * @return this potion recipe
	 */
	public IPotionRecipe register();
	
	/**
	 * Gets the input stack of this {@link IPotionRecipe}.
	 * 
	 * @return the input stack
	 */
	public ItemStack getInput();
	
	/**
	 * Returns true if this {@link IPotionRecipe} is appliable to the given
	 * {@link ItemStack} {@code potion}.
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @param potion
	 *            the potion stack
	 * @return true, if this potion recipe is appliable
	 */
	public boolean canApply(ItemStack ingredient, PotionTypeList potionTypes);
	
	/**
	 * Applies this {@link IPotionRecipe} to the given {@link ItemStack}
	 * {@code potion}.
	 * 
	 * @param potion
	 *            the potion stack
	 */
	public void apply(PotionTypeList potionTypes);
}
