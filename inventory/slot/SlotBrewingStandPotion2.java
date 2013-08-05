package clashsoft.brewingapi.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class SlotBrewingStandPotion2 extends Slot
{
	/** The player that has this container open. */
	private EntityPlayer	player;
	
	public SlotBrewingStandPotion2(EntityPlayer par1EntityPlayer, IInventory par2IInventory, int par3, int par4, int par5)
	{
		super(par2IInventory, par3, par4, par5);
		this.player = par1EntityPlayer;
	}
	
	/**
	 * Check if the stack is a valid item for this slot. Always true beside for
	 * the armor slots.
	 */
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		return canHoldPotion(par1ItemStack);
	}
	
	/**
	 * Returns the maximum stack size for a given slot (usually the same as
	 * getInventoryStackLimit(), but 1 in the case of armor slots)
	 */
	public int getSlotStackLimit()
	{
		return 1;
	}
	
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
	{
		if (par2ItemStack.itemID == Item.potion.itemID && par2ItemStack.getItemDamage() > 0)
		{
			this.player.addStat(AchievementList.potion, 1);
		}
		
		super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
	}
	
	/**
	 * Returns true if this itemstack can be filled with a potion
	 */
	public static boolean canHoldPotion(ItemStack par0ItemStack)
	{
		return par0ItemStack != null && par0ItemStack.itemID == Item.potion.itemID;
	}
}
