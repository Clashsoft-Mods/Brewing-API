package clashsoft.brewingapi.potion.recipe;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.potion.PotionList;
import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.base.IPotionBase;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;
import clashsoft.cslib.minecraft.stack.CSStacks;
import clashsoft.cslib.minecraft.stack.StackFactory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class PotionRecipes
{
	protected static final List<IPotionRecipe>	recipes	= new ArrayList();
	
	public static IPotionRecipe					amplify	= new PotionRecipeAmplify(StackFactory.create(Items.glowstone_dust)).register();
	public static IPotionRecipe					extend	= new PotionRecipeExtend(StackFactory.create(Items.redstone)).register();
	public static IPotionRecipe					dilute	= new PotionRecipeDilute(StackFactory.create(Items.water_bucket)).register();
	public static IPotionRecipe					splash	= new PotionRecipeSplash(StackFactory.create(Items.gunpowder)).register();
	public static IPotionRecipe					invert	= new PotionRecipeInvert(StackFactory.create(Items.fermented_spider_eye)).register();
	
	public static List<IPotionRecipe> getRecipes()
	{
		return recipes;
	}
	
	/**
	 * Checks if the given {@link ItemStack} {@code ingredient} is a valid
	 * potion ingredient.
	 * 
	 * @param ingredient
	 *            the igredient
	 * @return true, if the stack is a valid potion ingredient
	 */
	public static boolean isIngredient(ItemStack ingredient)
	{
		for (IPotionRecipe recipe : recipes)
		{
			if (CSStacks.equals(recipe.getIngredient(), ingredient))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the given {@link ItemStack} {@code ingredient} is applicable to
	 * the given {@link ItemStack} {@code potion}. It does that by going through
	 * the list of all {@link PotionRecipe}s and calling their
	 * {@link IPotionRecipe#canApply(ItemStack, PotionTypeList)}. If one of
	 * these calls returns {@code true}, the ingredient is applicable and
	 * {@code true} is returned.
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @param potion
	 *            the potion
	 * @return true, if the ingredient is applicable to the potion.
	 */
	public static boolean canApplyIngredient(ItemStack input, ItemStack ingredient)
	{
		return canApplyIngredient(new PotionTypeList(input), ingredient);
	}
	
	public static boolean canApplyIngredient(PotionTypeList input, ItemStack ingredient)
	{
		for (IPotionRecipe recipe : recipes)
		{
			if (recipe.canApply(ingredient, input))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Applies the given {@link ItemStack} {@code ingredient} to the given
	 * {@link ItemStack} {@code potion}. It does that by going through the list
	 * of all {@link PotionRecipe}s and calling their
	 * {@link IPotionRecipe#apply(PotionTypeList)} method.
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @param potion
	 *            the potion
	 * @return the potion with applied ingredients
	 */
	public static ItemStack applyIngredient(ItemStack input, ItemStack ingredient)
	{
		return applyIngredient(new PotionTypeList(input), ingredient);
	}
	
	public static ItemStack applyIngredient(PotionTypeList input, ItemStack ingredient)
	{
		for (IPotionRecipe recipe : recipes)
		{
			if (recipe.canApply(ingredient, input))
			{
				recipe.apply(ingredient, input);
			}
		}
		input.save();
		return input.getPotion();
	}
	
	/**
	 * Gets a {@link PotionRecipe} from the given {@link ItemStack}
	 * {@code ingredient}.
	 * 
	 * @param ingredient
	 *            the ingredient stack
	 * @return the potion recipe
	 */
	public static IPotionRecipe get(ItemStack ingredient)
	{
		for (IPotionRecipe recipe : recipes)
		{
			if (CSStacks.equals(ingredient, recipe.getIngredient()))
			{
				return recipe;
			}
		}
		return null;
	}
	
	/**
	 * Gets all {@link PotionRecipe PotionRecipes} from the given
	 * {@link ItemStack} {@code ingredient}.
	 * 
	 * @param ingredient
	 *            the ingredient stack
	 * @return the potion recipes
	 */
	public static List<IPotionRecipe> getAll(ItemStack ingredient)
	{
		List<IPotionRecipe> list = new ArrayList();
		for (IPotionRecipe recipe : recipes)
		{
			if (CSStacks.equals(ingredient, recipe.getIngredient()))
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
		int id = potionType.getPotionID();
		for (IPotionRecipe recipe : recipes)
		{
			if (recipe instanceof PotionRecipe)
			{
				PotionRecipe recipe2 = (PotionRecipe) recipe;
				IPotionType output = recipe2.getOutput();
				if (id == output.getPotionID())
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
		int id = potionType.getPotionID();
		List<PotionRecipe> list = new ArrayList();
		
		for (IPotionRecipe recipe : recipes)
		{
			if (recipe instanceof PotionRecipe)
			{
				PotionRecipe recipe2 = (PotionRecipe) recipe;
				IPotionType output = recipe2.getOutput();
				if (id == output.getPotionID())
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
	 * @param ingredient
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
	 * {@link ItemStack} {@code input}, the given {@link IPotionBase}
	 * {@code base} and the given {@link IPotionType} {@code potionType}.
	 * 
	 * @param ingredient
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
	 * {@code effect}. This automatically finds a {@link PotionType} for the
	 * effect.
	 * 
	 * @param ingredient
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
	 * {@link ItemStack} {@code input}, the given {@link IPotionBase}
	 * {@code base} and the given {@link PotionEffect} {@code effect}. This
	 * automatically finds a {@link PotionType} for the effect.
	 * 
	 * @param ingredient
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
