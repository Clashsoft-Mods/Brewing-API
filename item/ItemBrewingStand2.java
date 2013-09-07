package clashsoft.brewingapi.item;

import clashsoft.brewingapi.BrewingAPI;

import net.minecraft.item.ItemReed;

public class ItemBrewingStand2 extends ItemReed
{
	public ItemBrewingStand2(int id)
	{
		super(id, BrewingAPI.brewingStand2);
		this.setTextureName("brewing_stand");
	}
}
