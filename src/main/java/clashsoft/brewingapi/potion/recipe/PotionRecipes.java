package clashsoft.brewingapi.potion.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.TCustomHashMap;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionBase;
import clashsoft.cslib.minecraft.util.ItemStackHashingStrategy;

import net.minecraft.item.ItemStack;

public class PotionRecipes
{
	public static final TCustomHashMap<ItemStack, PotionRecipe>	potionRecipes	= new TCustomHashMap(ItemStackHashingStrategy.instance);
	
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
	 * {@code output}.
	 * 
	 * @param input
	 *            the input stack
	 * @param potionType
	 *            the output potion type
	 */
	public static void addRecipe(ItemStack ingredient, IPotionType potionType)
	{
		addRecipe(ingredient, null, potionType);
	}
	
	/**
	 * Creates and registers a new {@link PotionRecipe} from the given
	 * {@link ItemStack} {@code input}, the given {@link PotionBase}
	 * {@code base} and the given {@link IPotionType} {@code output}.
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
			registerRecipe(new PotionRecipe(ingredient, base, potionType));
	}
}
