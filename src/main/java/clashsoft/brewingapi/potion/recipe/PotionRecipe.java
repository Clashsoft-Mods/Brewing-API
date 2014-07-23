package clashsoft.brewingapi.potion.recipe;

import clashsoft.brewingapi.potion.PotionList;
import clashsoft.brewingapi.potion.base.IPotionBase;
import clashsoft.brewingapi.potion.base.PotionBase;
import clashsoft.brewingapi.potion.type.IPotionType;

import net.minecraft.item.ItemStack;

public class PotionRecipe extends AbstractPotionRecipe
{
	private IPotionType	output;
	private IPotionBase	base;
	
	/**
	 * Constructs a new {@link PotionRecipe} from the given {@link ItemStack}
	 * {@code input} and the given {@link IPotionType} {@code output}. The
	 * required base potion is set to awkward.
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
	public PotionRecipe(ItemStack input, IPotionBase base, IPotionType output)
	{
		super(input);
		this.base = base;
		this.output = output;
	}
	
	/**
	 * Gets the base potion of this {@link PotionRecipe}.
	 * 
	 * @return the base potion
	 */
	public IPotionBase getBase()
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
	
	@Override
	public boolean canApply(ItemStack potion)
	{
		IPotionBase base = this.base;
		if (base == null)
		{
			return true;
		}
		return base.matches(this.output, potion);
	}
	
	@Override
	public ItemStack apply(ItemStack potion)
	{
		if (potion.getItemDamage() == 0)
		{
			potion.setItemDamage(1);
		}
		return this.output.apply(potion);
	}
}
