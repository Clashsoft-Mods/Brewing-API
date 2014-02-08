package clashsoft.brewingapi.item;

import java.util.List;

import clashsoft.brewingapi.BrewingAPI;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;

public class ItemBrewingStand2 extends ItemReed
{
	public ItemBrewingStand2()
	{
		super(BrewingAPI.brewingStand2);
		this.setCreativeTab(CreativeTabs.tabBrewing);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
	{
		list.add("THIS IS BREWING API");
	}
}
