package clashsoft.brewingapi.item;

import clashsoft.brewingapi.BrewingAPI;

import net.minecraft.item.ItemGlassBottle;
import net.minecraft.util.Icon;

public class ItemGlassBottle2 extends ItemGlassBottle
{
	public ItemGlassBottle2(int par1)
	{
		super(par1);
		this.setTextureName("potion");
	}
	
	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	public Icon getIconFromDamage(int par1)
	{
		return BrewingAPI.potion2.getIconFromDamage(1);
	}
	
}
