package clashsoft.brewingapi.potion.recipe;

import java.util.List;

import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;
import clashsoft.cslib.minecraft.stack.StackFactory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PotionRecipeImprove extends AbstractPotionRecipe
{
	public PotionRecipeImprove()
	{
		super(StackFactory.create(Items.glowstone_dust));
	}
	
	@Override
	public boolean canApply(ItemStack potion)
	{
		List<IPotionType> types = PotionType.getPotionTypes(potion);
		for (IPotionType type : types)
		{
			if (type.isImprovable())
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ItemStack apply(ItemStack potion)
	{
		List<IPotionType> types = PotionType.getPotionTypes(potion);
		for (int i = 0; i < types.size(); i++)
		{
			IPotionType type = types.get(i);
			type = type.onImproved();
			types.set(i, type);
		}
		return potion;
	}
}
