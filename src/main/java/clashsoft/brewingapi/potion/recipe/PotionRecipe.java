package clashsoft.brewingapi.potion.recipe;

import java.util.List;

import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.potion.PotionList;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionBase;

import net.minecraft.item.ItemStack;

public class PotionRecipe
{
	private IPotionType	output;
	private PotionBase	base;
	private ItemStack	input;
	
	/**
	 * Constructs a new {@link PotionRecipe} from the given {@link ItemStack}
	 * {@code input} the given {@link IPotionType} {@code output}. The required
	 * base potion is set to awkward.
	 * 
	 * @param input
	 *            the input stack
	 * @param output
	 *            the output potion type
	 */
	public PotionRecipe(ItemStack input, IPotionType output)
	{
		this(input, PotionList.awkward, output);
	}
	
	/**
	 * Constructs a new {@link PotionRecipe} from the given {@link ItemStack}
	 * {@code input}, the given {@link PotionBase} {@code base} and the given
	 * {@link IPotionType} {@code output}.
	 * 
	 * @param input
	 *            the input stack
	 * @param output
	 *            the output potion type
	 */
	public PotionRecipe(ItemStack input, PotionBase base, IPotionType output)
	{
		this.input = input;
		this.base = base;
		this.output = output;
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
	 * Gets the base potion of this {@link PotionRecipe}.
	 * 
	 * @return the base potion
	 */
	public PotionBase getBase()
	{
		return this.base;
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
		PotionBase requiredBase = this.base;
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
				if (pt instanceof PotionBase)
				{
					if (requiredBase.equals(pt))
					{
						flag = true;
						pt.remove(potionStack);
					}
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
