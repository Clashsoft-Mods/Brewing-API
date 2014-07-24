package clashsoft.brewingapi.potion.recipe;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.cslib.minecraft.stack.StackFactory;

import net.minecraft.init.Items;

public class PotionRecipeInvert extends AbstractPotionRecipe
{
	public PotionRecipeInvert()
	{
		super(StackFactory.create(Items.fermented_spider_eye));
	}
	
	@Override
	public boolean canApply(PotionTypeList potionTypes)
	{
		for (IPotionType type : potionTypes)
		{
			if (type.isInversible())
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
			type = type.onInverted();
			potionTypes.set(i, type);
		}
	}
}
