package clashsoft.brewingapi.inventory.slot;

import clashsoft.brewingapi.brewing.PotionType;
import clashsoft.brewingapi.inventory.ContainerBrewingStand2;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotBrewingStandIngredient2 extends Slot
{
	/** The brewing stand this slot belongs to. */
	final ContainerBrewingStand2	brewingStand;
	
	public SlotBrewingStandIngredient2(ContainerBrewingStand2 par1ContainerBrewingStand, IInventory par2IInventory, int par3, int par4, int par5)
	{
		super(par2IInventory, par3, par4, par5);
		this.brewingStand = par1ContainerBrewingStand;
	}
	
	/**
	 * Check if the stack is a valid item for this slot. Always true beside for
	 * the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		if (par1ItemStack != null)
		{
			if (Item.itemsList[par1ItemStack.itemID].isPotionIngredient() || PotionType.isPotionIngredient(par1ItemStack))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the maximum stack size for a given slot (usually the same as
	 * getInventoryStackLimit(), but 1 in the case of armor slots)
	 */
	@Override
	public int getSlotStackLimit()
	{
		return 64;
	}
}
