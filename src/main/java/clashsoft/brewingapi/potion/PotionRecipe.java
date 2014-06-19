package clashsoft.brewingapi.potion;

import gnu.trove.map.hash.TCustomHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionBase;
import clashsoft.cslib.minecraft.util.ItemStackHashingStrategy;

import net.minecraft.item.ItemStack;

public class PotionRecipe
{
	public static final TCustomHashMap<ItemStack, PotionRecipe>	potionRecipes	= new TCustomHashMap(ItemStackHashingStrategy.instance);
	
	private IPotionType											output;
	private ItemStack											input;
	
	/**
	 * Constructs a new {@link PotionRecipe} from the given {@link ItemStack}
	 * {@code input} and the given {@link IPotionType} {@code output}.
	 * 
	 * @param input
	 *            the input stack
	 * @param output
	 *            the output potion type
	 */
	public PotionRecipe(ItemStack input, IPotionType output)
	{
		this.input = input;
		this.output = output;
	}
	
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
	public static ItemStack getIngredient(IPotionType potionType)
	{
		for (Map.Entry entry : potionRecipes.entrySet())
		{
			if (potionType.equals(entry.getValue()))
			{
				return (ItemStack) entry.getKey();
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
	public static List<ItemStack> getIngredients(IPotionType type)
	{
		List list = new ArrayList();
		
		for (Map.Entry entry : potionRecipes.entrySet())
		{
			if (type.equals(entry.getValue()))
			{
				list.add(entry.getKey());
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
		potionRecipes.put(recipe.input, recipe);
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
		if (ingredient != null)
			registerRecipe(new PotionRecipe(ingredient, potionType));
	}
	
	/**
	 * Gets the input stack of this {@link PotionRecipe}.
	 * 
	 * @return the input stack
	 */
	public ItemStack getInput()
	{
		return this.input;
	}
	
	/**
	 * Gets the output potion type of this {@link PotionRecipe}.
	 * 
	 * @return the output potion type
	 */
	public IPotionType getOutput()
	{
		return this.output;
	}
	
	/**
	 * Applies this {@link PotionRecipe} to the given {@link ItemStack}
	 * {@code potionStack}.
	 * <p>
	 * If the output potion type of this has a required {@link PotionBase}, it
	 * searches the existent {@link IPotionType PotionTypes} of the potion stack
	 * for that base. If it fails to find the required base, the output potion
	 * type is not applied to the potion stack, returning the unmodified potion
	 * stack.<br>
	 * If it doesn't have a required base, the output potion type gets applied
	 * if the potion stack has no other potion types applied to it.
	 * 
	 * @param potionStack
	 *            the potion stack
	 * @return the potion stack
	 */
	public ItemStack apply(ItemStack potionStack)
	{
		PotionBase requiredBase = this.output.getBase();
		boolean flag = false;
		
		List<IPotionType> potionTypes = ((ItemPotion2) potionStack.getItem()).getPotionTypes(potionStack);
		
		if (requiredBase == null)
		{
			flag = potionTypes.isEmpty();
		}
		else
		{
			for (IPotionType pt : potionTypes)
			{
				if (pt instanceof PotionBase && ((PotionBase) pt).equals(requiredBase))
				{
					flag = true;
					pt.remove(potionStack);
				}
			}
		}
		
		if (flag)
		{
			return this.output.apply(potionStack);
		}
		return potionStack;
	}
}
