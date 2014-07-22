package clashsoft.brewingapi.potion.recipe;

import java.util.List;

import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;
import clashsoft.cslib.minecraft.stack.StackFactory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PotionRecipeSplash extends AbstractPotionRecipe
{
	public PotionRecipeSplash()
	{
		super(StackFactory.create(Items.gunpowder));
	}
	
	@Override
	public boolean canApply(ItemStack potion)
	{
		return ((ItemPotion2) potion.getItem()).isSplash(potion);
	}
	
	@Override
	public ItemStack apply(ItemStack potion)
	{
		List<IPotionType> types = PotionType.getPotionTypes(potion);
		for (int i = 0; i < types.size(); i++)
		{
			IPotionType type = types.get(i);
			type = type.onGunpowderUsed();
			types.set(i, type);
		}
		((ItemPotion2) potion.getItem()).setSplash(potion, true);
		return potion;
	}
}
