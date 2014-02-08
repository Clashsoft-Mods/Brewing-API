package clashsoft.brewingapi.inventory.slot;

import clashsoft.brewingapi.brewing.PotionType;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBrewingStandIngredient2 extends Slot
{
	public SlotBrewingStandIngredient2(IInventory inventory, int slotID, int x, int y)
	{
		super(inventory, slotID, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if (stack != null)
		{
			if (stack.getItem().isPotionIngredient(stack) || PotionType.isPotionIngredient(stack))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 64;
	}
}
