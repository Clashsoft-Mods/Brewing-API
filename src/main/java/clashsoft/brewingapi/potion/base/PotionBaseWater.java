package clashsoft.brewingapi.potion.base;

import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.type.IPotionType;

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
		return potion;
	}
	
	@Override
	public ItemStack apply(PotionTypeList potionTypes)
	{
		return potionTypes.getPotion();
	}
	
	@Override
	public boolean accepts(IPotionType potion)
	{
		return potion instanceof IPotionBase;
	}
	
	@Override
	public boolean matches(IPotionType type, PotionTypeList potionTypes)
	{
		return potionTypes.isWater();
	}
	
	@Override
	public void onApplied(IPotionType type, PotionTypeList potionTypes)
	{
	}
}
