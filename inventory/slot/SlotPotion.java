package clashsoft.brewingapi.inventory.slot;

import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class SlotPotion extends Slot
{
	/** The player that has this container open. */
	private EntityPlayer	player;
	
	public SlotPotion(EntityPlayer player, IInventory inventory, int slotID, int x, int y)
	{
		super(inventory, slotID, x, y);
		this.player = player;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return canHoldPotion(stack);
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
	{
		if (stack.getItem() == Items.potionitem && stack.getItemDamage() > 0)
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
