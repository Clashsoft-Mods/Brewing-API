package clashsoft.brewingapi.potion.recipe;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.cslib.minecraft.stack.StackFactory;

import net.minecraft.init.Items;

public class PotionRecipeAmplify extends AbstractPotionRecipe
{
	public PotionRecipeAmplify()
	{
		super(StackFactory.create(Items.glowstone_dust));
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
