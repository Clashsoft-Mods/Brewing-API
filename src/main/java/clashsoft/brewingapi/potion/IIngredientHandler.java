package clashsoft.brewingapi.potion;

import net.minecraft.item.ItemStack;

public interface IIngredientHandler
{
	/**
	 * Returns true if this handler can handle the given {@link ItemStack}
	 * {@code ingredient}.
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @return true, if this can handle the ingredient.
	 */
	public boolean canHandleIngredient(ItemStack ingredient);
	
	/**
	 * Applies the given {@link ItemStack} {@code ingredient} to the given
	 * {@link ItemStack} {@code potion}. This should usually return the potion.
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @param potion
	 *            the potion stack
	 * @return the output stack
	 */
	public ItemStack applyIngredient(ItemStack ingredient, ItemStack potion);
	
	/**
	 * Returns true if this handler can apply the given {@link ItemStack}
	 * {@code ingredient} to the given {@link ItemStack} {@code potion}.
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @param potion
	 *            the potion stack
	 * @return true, if this can apply the ingredient.
	 */
	public boolean canApplyIngredient(ItemStack ingredient, ItemStack potion);
}
