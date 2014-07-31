package clashsoft.brewingapi.potion.recipe;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.type.IPotionType;

import net.minecraft.item.ItemStack;

public class PotionRecipeAmplify extends AbstractPotionRecipe
{
	public PotionRecipeAmplify(ItemStack ingredient)
	{
		super(ingredient);
	}
	
	@Override
	public boolean canApply(PotionTypeList potionTypes)
	{
		for (IPotionType type : potionTypes)
		{
			if (type.isAmplifiable())
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void apply(PotionTypeList potionTypes)
	{
		for (int i = 0; i < potionTypes.size(); i++)
		{
			IPotionType type = potionTypes.get(i);
			type = type.onAmplified();
			potionTypes.set(i, type);
		}
	}
}
