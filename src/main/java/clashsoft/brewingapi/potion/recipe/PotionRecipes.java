package clashsoft.brewingapi.potion.recipe;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.potion.PotionList;
import clashsoft.brewingapi.potion.base.IPotionBase;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionBase;
import clashsoft.brewingapi.potion.type.PotionType;
import clashsoft.cslib.minecraft.stack.CSStacks;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class PotionRecipes
{
	public static final List<IPotionRecipe>	recipes	= new ArrayList();
	
	public static IPotionRecipe amplify = new PotionRecipeAmplify().register();
	public static IPotionRecipe extend = new PotionRecipeExtend().register();
	public static IPotionRecipe dilute = new PotionRecipeDilute().register();
	public static IPotionRecipe splash = new PotionRecipeSplash().register();
	public static IPotionRecipe invert = new PotionRecipeInvert().register();
	
	/**
	 * Gets a {@link PotionRecipe} from the given {@link ItemStack}
	 * {@code input}.
	 * 
	 * @param input
	 *            the input stack
	 * @return the potion recipe
	 */
	public static IPotionRecipe get(ItemStack input)
	{
		for (IPotionRecipe recipe : recipes)
		{
			if (CSStacks.equals(input, recipe.getInput()))
			{
				return recipe;
			}
		}
		return null;
	}
	
	/**
	 * Gets all {@link PotionRecipe PotionRecipes} from the given
	 * {@link ItemStack} {@code input}.
	 * 
	 * @param input
	 *            the input stack
	 * @return the potion recipes
	 */
	public static List<IPotionRecipe> getAll(ItemStack input)
	{
		List<IPotionRecipe> list = new ArrayList();
		for (IPotionRecipe recipe : recipes)
		{
			if (CSStacks.equals(input, recipe.getInput()))
			{
				list.add(recipe);
			}
		}
		return list;
	}
	
	/**
	 * Returns the ingredient that can be used to brew the given
	 * {@link IPotionType} {@code potionType}.
	 * 
	 * @param potionType
	 *            the potion type
	 * @return the ingredient
	 */
	public static PotionRecipe get(IPotionType potionType)
	{
		for (IPotionRecipe recipe : recipes)
		{
			if (recipe instanceof PotionRecipe)
			{
				PotionRecipe recipe2 = (PotionRecipe) recipe;
				IPotionType output = recipe2.getOutput();
				if (potionType.equals(output))
				{
					return recipe2;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns all ingredients that can be used to brew the given
	 * {@link IPotionType} {@code potionType} in a list.
	 * 
	 * @param potionType
	 *            the potion type
	 * @return the ingredients
	 */
	public static List<PotionRecipe> getAll(IPotionType potionType)
	{
		List<PotionRecipe> list = new ArrayList();
		
		for (IPotionRecipe recipe : recipes)
		{
			if (recipe instanceof PotionRecipe)
			{
				PotionRecipe recipe2 = (PotionRecipe) recipe;
				IPotionType output = recipe2.getOutput();
				if (potionType.equals(output))
				{
					list.add(recipe2);
				}
			}
		}
		return list;
	}
	
	/**
	 * Creates and registers a new {@link PotionRecipe} from the given
	 * {@link ItemStack} {@code input} and the given {@link IPotionType}
	 * {@code potionType}.
	 * 
	 * @param input
	 *            the input stack
	 * @param potionType
	 *            the output potion type
	 */
	public static void addRecipe(ItemStack ingredient, IPotionType potionType)
	{
		addRecipe(ingredient, PotionList.awkward, potionType);
	}
	
	/**
	 * Creates and registers a new {@link PotionRecipe} from the given
	 * {@link ItemStack} {@code input}, the given {@link PotionBase}
	 * {@code base} and the given {@link IPotionType} {@code potionType}.
	 * 
	 * @param input
	 *            the input stack
	 * @param base
	 *            the required potion base
	 * @param potionType
	 *            the output potion type
	 */
	public static void addRecipe(ItemStack ingredient, IPotionBase base, IPotionType potionType)
	{
		if (ingredient != null && base != null)
		{
			recipes.add(new PotionRecipe(ingredient, base, potionType));
		}
	}
	
	/**
	 * Creates and registers a new {@link PotionRecipe} from the given
	 * {@link ItemStack} {@code input} and the given {@link PotionEffect}
	 * {@code effect}.This automatically finds a {@link PotionType} for the
	 * effect.
	 * 
	 * @param input
	 *            the input stack
	 * @param effect
	 *            the output effect
	 */
	public static void addRecipe(ItemStack ingredient, PotionEffect effect)
	{
		addRecipe(ingredient, PotionList.awkward, effect);
	}
	
	/**
	 * Creates and registers a new {@link PotionRecipe} from the given
	 * {@link ItemStack} {@code input}, the given {@link PotionBase}
	 * {@code base} and the given {@link PotionEffect} {@code effect}. This
	 * automatically finds a {@link PotionType} for the effect.
	 * 
	 * @param input
	 *            the input stack
	 * @param base
	 *            the required potion base
	 * @param effect
	 *            the output effect
	 */
	public static void addRecipe(ItemStack ingredient, IPotionBase base, PotionEffect effect)
	{
		if (ingredient != null && base != null)
		{
			recipes.add(new PotionRecipe(ingredient, base, PotionType.getFromEffect(effect)));
		}
	}
}
