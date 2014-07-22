package clashsoft.brewingapi.potion.recipe;

import java.util.List;

import clashsoft.brewingapi.potion.attribute.IPotionAttribute;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;

import net.minecraft.item.ItemStack;

public class PotionRecipeAttribute extends AbstractPotionRecipe
{
	private IPotionAttribute	attribute;
	
	/**
	 * Constructs a new {@link PotionRecipeAttribute} from the given
	 * {@link ItemStack} {@code input} and the given {@link IPotionAttribute}
	 * {@code attribute}.
	 * 
	 * @param input
	 *            the input stack
	 * @param attribute
	 *            the attribute
	 */
	public PotionRecipeAttribute(ItemStack input, IPotionAttribute attribute)
	{
		super(input);
		this.attribute = attribute;
	}
	
	@Override
	public boolean canApply(ItemStack potion)
	{
		return true;
	}
	
	@Override
	public ItemStack apply(ItemStack potion)
	{
		List<IPotionType> potionTypes = PotionType.getPotionTypes(potion);
		for (IPotionType potionType : potionTypes)
		{
			potionType.addAttribute(this.attribute);
		}
		return potion;
	}
}
