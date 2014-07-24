package clashsoft.brewingapi.potion.recipe;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.cslib.minecraft.stack.StackFactory;

import net.minecraft.init.Items;

public class PotionRecipeSplash extends AbstractPotionRecipe
{
	public PotionRecipeSplash()
	{
		super(StackFactory.create(Items.gunpowder));
	}
	
	@Override
	public boolean canApply(PotionTypeList potionTypes)
	{
		return !potionTypes.isSplash();
	}
	
	@Override
	public void apply(PotionTypeList potionTypes)
	{
		for (int i = 0; i < potionTypes.size(); i++)
		{
			IPotionType type = potionTypes.get(i);
			type = type.onGunpowderUsed();
			potionTypes.set(i, type);
		}
		potionTypes.setSplash(true);
	}
}
