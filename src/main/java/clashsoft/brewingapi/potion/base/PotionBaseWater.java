package clashsoft.brewingapi.potion.base;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.item.ItemStack;

public class PotionBaseWater implements IPotionBase
{
	@Override
	public IPotionBase register()
	{
		bases.put("water", this);
		return this;
	}
	
	@Override
	public String getName()
	{
		return "water";
	}
	
	@Override
	public int getLiquidColor()
	{
		return 0x0C0CFF;
	}
	
	@Override
	public ItemStack apply(ItemStack potion)
	{
		return new ItemStack(BrewingAPI.potion2);
	}
	
	@Override
	public boolean matches(ItemStack potion)
	{
		return ((ItemPotion2) potion.getItem()).isWater(potion);
	}
}
