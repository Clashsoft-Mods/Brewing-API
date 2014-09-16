package com.agadar.brewingapi;

import java.util.List;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.recipe.CustomPotionRecipe;
import clashsoft.brewingapi.potion.recipe.PotionRecipes;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;
import clashsoft.cslib.logging.CSLog;

import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;

/**
 * Manages all brewing recipes.
 */
public class BrewingRecipes
{
	private static final BrewingRecipes	brewingBase	= new BrewingRecipes();
	
	/**
	 * Returns the instance.
	 */
	public static BrewingRecipes brewing()
	{
		return brewingBase;
	}
	
	/**
	 * Adds a new brewing recipe, where applying the ingredient to the input
	 * results in the output.
	 * 
	 * @param input
	 *            the input potion
	 * @param ingredient
	 *            the ingredient
	 * @param output
	 *            the output potion
	 */
	public void addBrewing(ItemStack input, ItemStack ingredient, ItemStack output)
	{
		if (input == null || input.stackSize <= 0 || ingredient == null || ingredient.stackSize <= 0 || output == null || output.stackSize <= 0)
		{
			CSLog.error("Error while adding a brewing recipe - the ItemStacks may not be null or have a stack size smaller than 1.");
			return;
		}
		
		if (!(input.getItem() instanceof ItemPotion) || !(output.getItem() instanceof ItemPotion))
		{
			CSLog.error("Error while adding a brewing recipe - the Items of the input and the output ItemStacks have to be instances of ItemPotion.");
			return;
		}
		
		new CustomPotionRecipe(input, ingredient, output).register();
	}
	
	/**
	 * Returns whether the given {@link ItemStack} is a valid ingredient for any
	 * brewing recipe.
	 * 
	 * @param ingredient
	 *            the ingredient
	 * @return true, if the ingredient is a valid potion ingredient.
	 */
	public boolean isPotionIngredient(ItemStack ingredient)
	{
		return PotionRecipes.isIngredient(ingredient);
	}
	
	/**
	 * Returns the result of applying the given ingredient to the given input.
	 * Returns null if the brewing recipe does not exist.
	 * 
	 * @param input
	 *            the input potion
	 * @param ingredient
	 *            the ingredient
	 * @return the output
	 */
	public ItemStack getBrewingResult(ItemStack input, ItemStack ingredient)
	{
		return PotionRecipes.applyIngredient(input, ingredient);
	}
	
	/**
	 * Adds the given {@link PotionEffect} to an {@link NBTTagList} and adds it
	 * to the given {@link ItemStack}'s NBT.
	 * 
	 * @param stack
	 *            the potion stack
	 * @param effect
	 *            the effect
	 */
	public void addEffect(ItemStack stack, PotionEffect effect)
	{
		PotionTypeList potionTypes = new PotionTypeList(stack);
		potionTypes.add(PotionType.getFromEffect(effect));
		potionTypes.save();
	}
	
	/**
	 * Translates the given {@link List} of {@link PotionEffect}s to an
	 * {@link NBTTagList} and adds it to the given {@link ItemStack}'s NBT.
	 * 
	 * @param stack
	 *            the potion stack
	 * @param effects
	 *            the list of effects
	 */
	public void setEffects(ItemStack stack, List<PotionEffect> effects)
	{
		PotionTypeList potionTypes = new PotionTypeList(stack);
		for (PotionEffect effect : effects)
		{
			IPotionType type = PotionType.getFromEffect(effect);
			potionTypes.add(type);
		}
		potionTypes.save();
	}
	
	/**
	 * Calculates the duration modifier for a potion effect according to the
	 * given parameters.
	 * 
	 * @param splash
	 *            true, if the potion is a splash potion
	 * @param amplifier
	 *            the amplifier
	 * @param extended
	 *            true, if the potion is extended
	 * @return the duration multiplier of the potion
	 */
	public float getDurationModifier(boolean splash, int amplifier, boolean extended)
	{
		float modifier = splash ? 0.75F : 1.0F;
		modifier /= 2 << amplifier;
		return extended ? modifier * 8F / 3F : modifier;
	}
}