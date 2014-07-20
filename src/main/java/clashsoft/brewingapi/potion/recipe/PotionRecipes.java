package clashsoft.brewingapi.potion.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clashsoft.brewingapi.potion.PotionList;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionBase;
import clashsoft.brewingapi.potion.type.PotionType;
import clashsoft.cslib.minecraft.stack.ItemStackHashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class PotionRecipes
{
	public static final Map<ItemStack, PotionRecipe>	potionRecipes	= new ItemStackHashMap();
	
	/**
	 * Gets a {@link PotionRecipe} from the given {@link ItemStack}
	 * {@code input}.
	 * 
	 * @param input
	 *            the input stack
	 * @return the potion recipe
	 */
	public static PotionRecipe get(ItemStack input)
	{
		return potionRecipes.get(input);
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
		for (Map.Entry entry : potionRecipes.entrySet())
		{
			PotionRecipe recipe = (PotionRecipe) entry.getValue();
			if (potionType.equals(recipe.getOutput()))
			{
				return recipe;
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
	 * @return the ingredient
	 */
	public static List<PotionRecipe> getAll(IPotionType potionType)
	{
		List list = new ArrayList();
		
		for (Map.Entry entry : potionRecipes.entrySet())
		{
			PotionRecipe recipe = (PotionRecipe) entry.getValue();
			if (potionType.equals(recipe.getOutput()))
			{
				list.add(recipe);
			}
		}
		return list;
	}
	
	/**
	 * Registers a new {@link PotionRecipe}.
	 * 
	 * @param recipe
	 *            the recipe
	 */
	public static void registerRecipe(PotionRecipe recipe)
	{
		potionRecipes.put(recipe.getInput(), recipe);
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
	public static void addRecipe(ItemStack ingredient, PotionBase base, IPotionType potionType)
	{
		if (ingredient != null)
		{
			registerRecipe(new PotionRecipe(ingredient, base, potionType));
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
	public static void addRecipe(ItemStack ingredient, PotionBase base, PotionEffect effect)
	{
		if (ingredient != null)
		{
			registerRecipe(new PotionRecipe(ingredient, base, PotionType.getFromEffect(effect)));
		}
	}
}
