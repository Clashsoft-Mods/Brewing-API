package clashsoft.brewingapi.inventory;

import clashsoft.brewingapi.inventory.slot.SlotBrewingStandIngredient2;
import clashsoft.brewingapi.inventory.slot.SlotPotion;
import clashsoft.brewingapi.tileentity.TileEntityBrewingStand2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBrewingStand2 extends Container
{
	private TileEntityBrewingStand2	brewingStand;
	
	/** Instance of Slot. */
	private final Slot				theSlot;
	private int						brewTime	= 0;
	
	public ContainerBrewingStand2(InventoryPlayer inventory, TileEntityBrewingStand2 brewingStand)
	{
		this.brewingStand = brewingStand;
		this.addSlotToContainer(new SlotPotion(inventory.player, brewingStand, 0, 56, 46));
		this.addSlotToContainer(new SlotPotion(inventory.player, brewingStand, 1, 79, 53));
		this.addSlotToContainer(new SlotPotion(inventory.player, brewingStand, 2, 102, 46));
		this.theSlot = this.addSlotToContainer(new SlotBrewingStandIngredient2(brewingStand, 3, 79, 17));
		int i;
		
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		
		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting icrafting)
	{
		super.addCraftingToCrafters(icrafting);
		icrafting.sendProgressBarUpdate(this, 0, this.brewingStand.getBrewTime());
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		
		for (int i = 0; i < this.crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting) this.crafters.get(i);
			
			if (this.brewTime != this.brewingStand.getBrewTime())
			{
				icrafting.sendProgressBarUpdate(this, 0, this.brewingStand.getBrewTime());
			}
		}
		
		this.brewTime = this.brewingStand.getBrewTime();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int slotID, int value)
	{
		if (slotID == 0)
		{
			this.brewingStand.setBrewTime(value);
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return this.brewingStand.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);
		
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if (slotID > 3)
			{
				if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(itemstack1))
				{
					if (!this.mergeItemStack(itemstack1, 3, 4, false))
					{
						return null;
					}
				}
				else if (SlotPotion.canHoldPotion(itemstack))
				{
					if (!this.mergeItemStack(itemstack1, 0, 3, false))
					{
						return null;
					}
				}
				else if (slotID >= 4 && slotID < 31)
				{
					if (!this.mergeItemStack(itemstack1, 31, 40, false))
					{
						return null;
					}
				}
				else if (slotID >= 31 && slotID < 40)
				{
					if (!this.mergeItemStack(itemstack1, 4, 31, false))
					{
						return null;
					}
				}
				else if (!this.mergeItemStack(itemstack1, 4, 40, false))
				{
					return null;
				}
			}
			else
			{
				if (!this.mergeItemStack(itemstack1, 4, 40, true))
				{
					return null;
				}
				
				slot.onSlotChange(itemstack1, itemstack);
			}
			
			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack) null);
			}
			else
			{
				slot.onSlotChanged();
			}
			
			if (itemstack1.stackSize == itemstack.stackSize)
			{
				return null;
			}
			
			slot.onPickupFromSlot(player, itemstack1);
		}
		
		return itemstack;
	}
}
