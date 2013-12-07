package clashsoft.brewingapi.inventory.slot;

import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class SlotPotion extends Slot
{
	/** The player that has this container open. */
	private EntityPlayer	player;
	
	public SlotPotion(EntityPlayer player, IInventory inventory, int slotIndex, int x, int y)
	{
		super(inventory, slotIndex, x, y);
		this.player = player;
	}
	
	/**
	 * Check if the stack is a valid item for this slot. Always true beside for
	 * the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return canHoldPotion(stack);
	}
	
	/**
	 * Returns the maximum stack size for a given slot (usually the same as
	 * getInventoryStackLimit(), but 1 in the case of armor slots)
	 */
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
	{
		if (stack.itemID == Item.potion.itemID && stack.getItemDamage() > 0)
		{
			this.player.addStat(AchievementList.potion, 1);
		}
		
		super.onPickupFromSlot(player, stack);
	}
	
	/**
	 * Returns true if this itemstack can be filled with a potion
	 */
	public static boolean canHoldPotion(ItemStack stack)
	{
		return stack != null && stack.getItem() instanceof ItemPotion2;
	}
}
