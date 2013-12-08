package clashsoft.brewingapi.tileentity;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.api.IIngredientHandler;
import clashsoft.brewingapi.brewing.PotionBase;
import clashsoft.brewingapi.brewing.PotionType;
import clashsoft.brewingapi.item.ItemPotion2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.MathHelper;

public class TileEntityBrewingStand2 extends TileEntityBrewingStand implements ISidedInventory
{
	public EntityPlayer			thePlayer			= null;
	
	/** The itemstacks currently placed in the slots of the brewing stand */
	private ItemStack[]			brewingItemStacks	= new ItemStack[4];
	private int					brewTime;
	
	/**
	 * an integer with each bit specifying whether that slot of the stand contains a potion
	 */
	private int					filledSlots;
	private ItemStack			ingredient;
	
	public static int			maxBrewTime			= 400;
	
	public TileEntityBrewingStand2()
	{
		
	}
	
	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory()
	{
		return this.brewingItemStacks.length;
	}
	
	/**
	 * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count ticks and creates a new spawn inside its implementation.
	 */
	@Override
	public void updateEntity()
	{
		if (this.brewTime > 0)
		{
			--this.brewTime;
			
			if (this.brewTime == 0)
			{
				this.brewPotions();
				this.spawnXP();
				this.onInventoryChanged();
			}
			else if (!this.canBrew())
			{
				this.brewTime = 0;
				this.onInventoryChanged();
			}
			else if (this.ingredient != this.brewingItemStacks[3])
			{
				this.brewTime = 0;
				this.onInventoryChanged();
			}
		}
		else if (this.canBrew())
		{
			this.brewTime = this.getMaxBrewTime();
			this.ingredient = this.brewingItemStacks[3];
		}
		
		int var1 = this.getFilledSlots();
		
		if (var1 != this.filledSlots)
		{
			this.filledSlots = var1;
			this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, var1, 2);
		}
		
		super.updateEntity();
	}
	
	private void spawnXP()
	{
		if (this.thePlayer != null && !this.thePlayer.worldObj.isRemote)
		{
			int i = this.getPotions();
			float f = 0F;
			int j;
			for (ItemStack is : this.brewingItemStacks)
			{
				if (is != null && is.getItem() instanceof ItemPotion2)
				{
					f += PotionType.getExperience(is);
				}
			}
			
			if (f == 0.0F)
			{
				i = 0;
			}
			else if (f < 1.0F)
			{
				j = MathHelper.floor_float(i * f);
				
				if (j < MathHelper.ceiling_float_int(i * f) && (float) Math.random() < i * f - j)
				{
					++j;
				}
				
				i = j;
			}
			
			while (i > 0)
			{
				j = EntityXPOrb.getXPSplit(i);
				i -= j;
				this.thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5D, this.thePlayer.posZ + 0.5D, j));
			}
		}
	}
	
	private int getPotions()
	{
		int i = 0;
		for (ItemStack is : this.brewingItemStacks)
		{
			if (is != null && is.getItem() instanceof ItemPotion2)
			{
				i++;
			}
		}
		return i;
	}
	
	public int getMaxBrewTime()
	{
		if (this.thePlayer != null && this.thePlayer.capabilities.isCreativeMode)
		{
			return 60;
		}
		return 400;
	}
	
	@Override
	public int getBrewTime()
	{
		return this.brewTime;
	}
	
	private boolean canBrew()
	{
		if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0)
		{
			this.ingredient = this.brewingItemStacks[3];
			
			if (!Item.itemsList[this.ingredient.itemID].isPotionIngredient() && !PotionType.isPotionIngredient(this.ingredient))
				return false;
			
			for (int potionIndex = 0; potionIndex < 3; ++potionIndex)
			{
				if (this.brewingItemStacks[potionIndex] != null)
				{
					ItemStack potion = this.brewingItemStacks[potionIndex];
					ItemPotion2 item = (ItemPotion2) potion.getItem();
					int damage = potion.getItemDamage();
					boolean water = item.isWater(damage);
					List<PotionType> potionTypes = item.getEffects(potion);
					
					if (this.ingredient.getItem() == Item.glowstone && !water)
					{
						for (int i = 0; i < potionTypes.size(); i++)
							if (potionTypes.get(i).isImprovable())
								return true;
					}
					else if (this.ingredient.getItem() == Item.redstone && !water)
					{
						for (int i = 0; i < potionTypes.size(); i++)
							if (potionTypes.get(i).isExtendable())
								return true;
					}
					else if (this.ingredient.getItem() == Item.fermentedSpiderEye && !water)
					{
						for (int i = 0; i < potionTypes.size(); i++)
							if (potionTypes.get(i).isInversible())
								return true;
					}
					else if (this.ingredient.getItem() == Item.gunpowder)
					{
						if (!item.isSplash(damage))
							return true;
					}
					else
						return PotionType.canApplyIngredient(this.ingredient, potion);
				}
			}
		}
		return false;
	}
	
	private void brewPotions()
	{	
		this.ingredient = this.brewingItemStacks[3];
		
		for (int potionIndex = 0; potionIndex < 3; ++potionIndex)
		{
			if (this.brewingItemStacks[potionIndex] != null)
			{
				ItemStack potion = this.brewingItemStacks[potionIndex];
				ItemPotion2 item = (ItemPotion2) potion.getItem();
				int damage = potion.getItemDamage();
				boolean water = item.isWater(damage);
				List<PotionType> potionTypes = item.getEffects(potion);
				List<PotionType> newPotionTypes = new ArrayList(potionTypes.size());
				
				boolean flag = false;
				
				if (this.ingredient.getItem() == Item.gunpowder)
				{
					damage = item.setSplash(damage, true);
				}
				
				if (this.ingredient.getItem() == Item.glowstone && !water)
				{
					for (int i = 0; i < potionTypes.size(); i++)
						newPotionTypes.add(potionTypes.get(i).onImproved());
				}
				else if (this.ingredient.getItem() == Item.redstone && !water)
				{
					for (int i = 0; i < potionTypes.size(); i++)
						newPotionTypes.add(potionTypes.get(i).onExtended());
				}
				else if (this.ingredient.getItem() == Item.fermentedSpiderEye && !water)
				{
					for (int i = 0; i < potionTypes.size(); i++)
						newPotionTypes.add(potionTypes.get(i).onInverted());
				}
				else if (this.ingredient.getItem() == Item.gunpowder && !water)
				{
					for (int i = 0; i < potionTypes.size(); i++)
						newPotionTypes.add(potionTypes.get(i).onGunpowderUsed());
				}
				else
				{
					IIngredientHandler handler = PotionType.getHandlerForIngredient(this.ingredient);
					if (handler != null && handler.canApplyIngredient(this.ingredient, potion))
					{
						this.brewingItemStacks[potionIndex] = handler.applyIngredient(this.ingredient, potion);
						continue;
					}
					
					PotionType potionType = PotionType.getPotionTypeFromIngredient(this.ingredient);
					if (potionType != null)
					{
						PotionBase requiredBase = potionType.getBase();
						
						if (requiredBase == null)
							newPotionTypes.add(potionType);
						else
						{
							for (PotionType pt : potionTypes)
							{
								if (pt instanceof PotionBase)
								{
									String basename = ((PotionBase) pt).basename;
									if (basename.equals(requiredBase.basename))
									{
										newPotionTypes.add(potionType);
									}
								}
							}
						}
					}
				}
				
				potion.setItemDamage(damage | 1);
				
				if (potion.hasTagCompound())
					potion.getTagCompound().removeTag(PotionType.COMPOUND_NAME);
				
				for (PotionType potionType : newPotionTypes)
				{
					potionType.addPotionTypeToItemStack(potion);
				}
				
				this.brewingItemStacks[potionIndex] = potion;
			}
		}
		
		if (Item.itemsList[this.ingredient.itemID].hasContainerItem())
		{
			this.brewingItemStacks[3] = Item.itemsList[this.ingredient.itemID].getContainerItemStack(this.brewingItemStacks[3]);
		}
		else
		{
			--this.brewingItemStacks[3].stackSize;
			
			if (this.brewingItemStacks[3].stackSize <= 0)
			{
				this.brewingItemStacks[3] = null;
			}
		}
	}
	
	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
		this.brewingItemStacks = new ItemStack[this.getSizeInventory()];
		
		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");
			
			if (var5 >= 0 && var5 < this.brewingItemStacks.length)
			{
				this.brewingItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
		
		this.brewTime = par1NBTTagCompound.getShort("BrewTime");
	}
	
	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setShort("BrewTime", (short) this.brewTime);
		NBTTagList var2 = new NBTTagList();
		
		for (int var3 = 0; var3 < this.brewingItemStacks.length; ++var3)
		{
			if (this.brewingItemStacks[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.brewingItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}
		
		par1NBTTagCompound.setTag("Items", var2);
	}
	
	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return par1 >= 0 && par1 < this.brewingItemStacks.length ? this.brewingItemStacks[par1] : null;
	}
	
	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (par1 >= 0 && par1 < this.brewingItemStacks.length)
		{
			ItemStack var3 = this.brewingItemStacks[par1];
			this.brewingItemStacks[par1] = null;
			return var3;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (par1 >= 0 && par1 < this.brewingItemStacks.length)
		{
			ItemStack var2 = this.brewingItemStacks[par1];
			this.brewingItemStacks[par1] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		if (par1 >= 0 && par1 < this.brewingItemStacks.length)
		{
			this.brewingItemStacks[par1] = par2ItemStack;
		}
	}
	
	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}
	
	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		this.thePlayer = par1EntityPlayer;
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public void openChest()
	{
	}
	
	@Override
	public void closeChest()
	{
	}
	
	public void handlePacketData(int typeData, int[] intData)
	{
		TileEntityBrewingStand2 var1 = this;
		if (intData != null)
		{
			int pos = 0;
			if (intData.length < var1.brewingItemStacks.length * 3)
			{
				return;
			}
			for (int i = 0; i < var1.brewingItemStacks.length; i++)
			{
				if (intData[pos + 2] != 0)
				{
					ItemStack is = new ItemStack(intData[pos], intData[pos + 2], intData[pos + 1]);
					var1.brewingItemStacks[i] = is;
				}
				else
				{
					var1.brewingItemStacks[i] = null;
				}
				pos += 3;
			}
		}
	}
	
	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	public boolean isStackValidForSlot(int par1, ItemStack par2ItemStack)
	{
		return par1 == 3 ? Item.itemsList[par2ItemStack.itemID].isPotionIngredient() : par2ItemStack.itemID == Item.potion.itemID || par2ItemStack.itemID == Item.glassBottle.itemID;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setBrewTime(int par1)
	{
		this.brewTime = par1;
	}
	
	/**
	 * returns an integer with each bit specifying wether that slot of the stand contains a potion
	 */
	@Override
	public int getFilledSlots()
	{
		int i = 0;
		
		for (int j = 0; j < 3; ++j)
		{
			if (this.brewingItemStacks[j] != null)
			{
				i |= 1 << j;
			}
		}
		
		return i;
	}
}
