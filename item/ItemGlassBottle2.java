package clashsoft.brewingapi.item;

import clashsoft.brewingapi.BrewingAPI;

import net.minecraft.item.ItemGlassBottle;
import net.minecraft.util.IIcon;

public class ItemGlassBottle2 extends ItemGlassBottle
{
	public ItemGlassBottle2()
	{
		this.setTextureName("potion");
	}
	
	@Override
	public IIcon getIconFromDamage(int metadata)
	{
		return BrewingAPI.potion2.getIconFromDamage(1);
	}
}
