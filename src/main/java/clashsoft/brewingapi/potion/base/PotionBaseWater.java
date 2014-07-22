package clashsoft.brewingapi.potion.base;

import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.item.ItemStack;

public class PotionBaseWater implements IPotionBase
{
	@Override
	public IPotionBase register()
	{
		baseMap.put("water", this);
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
	public boolean matches(ItemStack potion)
	{
		return ((ItemPotion2) potion.getItem()).isWater(potion);
	}
}
