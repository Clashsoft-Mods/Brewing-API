package clashsoft.brewingapi.tileentity;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.api.IIngredientHandler;
import clashsoft.brewingapi.brewing.PotionBase;
import clashsoft.brewingapi.brewing.PotionType;
import clashsoft.brewingapi.item.ItemPotion2;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants;

public class TileEntityBrewingStand2 extends TileEntityBrewingStand implements ISidedInventory
{
	public EntityPlayer	thePlayer			= null;
	
	/** The itemstacks currently placed in the slots of the brewing stand */
	private ItemStack[]	brewingItemStacks	= new ItemStack[4];
	private int			brewTime;
	
	/**
	 * an integer with each bit specifying whether that slot of the stand contains a potion
	 */
	private int			filledSlots;
	private ItemStack	ingredient;
	
	public TileEntityBrewingStand2()
	{
		
	}
	
	public void spawnXP()
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
	
	public int getPotions()
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
	
	private boolean canBrew()
	{
		if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0)
		{
			this.ingredient = this.brewingItemStacks[3];
			
			if (!PotionType.isPotionIngredient(this.ingredient))
				return false;
			
			for (int potionIndex = 0; potionIndex < 3; ++potionIndex)
			{
				if (this.brewingItemStacks[potionIndex] != null)
				{
					ItemStack potion = this.brewingItemStacks[potionIndex];
					ItemPotion2 item = (ItemPotion2) potion.getItem();
					boolean water = item.isWater(potion);
					List<PotionType> potionTypes = item.getEffects(potion);
					
					if (this.ingredient.getItem() == Items.glowstone_dust && !water)
					{
						for (int i = 0; i < potionTypes.size(); i++)
						{
							if (potionTypes.get(i).isImprovable())
							{
								return true;
							}
						}
					}
					else if (this.ingredient.getItem() == Items.redstone && !water)
					{
						for (int i = 0; i < potionTypes.size(); i++)
						{
							if (potionTypes.get(i).isExtendable())
							{
								return true;
							}
						}
					}
					else if (this.ingredient.getItem() == Items.fermented_spider_eye && !water)
					{
						for (int i = 0; i < potionTypes.size(); i++)
						{
							if (potionTypes.get(i).isInversible())
							{
								return true;
							}
						}
					}
					else if (this.ingredient.getItem() == Items.gunpowder)
					{
						if (!item.isSplash(potion))
						{
							return true;
						}
					}
					else
					{
						return PotionType.canApplyIngredient(this.ingredient, potion);
					}
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
				boolean water = item.isWater(potion);
				List<PotionType> potionTypes = item.getEffects(potion);
				List<PotionType> newPotionTypes = new ArrayList(potionTypes.size());
				
				boolean flag = false;
				
				if (this.ingredient.getItem() == Items.gunpowder)
				{
					damage = item.setSplash(potion, true);
				}
				
				if (this.ingredient.getItem() == Items.glowstone_dust && !water)
				{
					for (int i = 0; i < potionTypes.size(); i++)
					{
						newPotionTypes.add(potionTypes.get(i).onImproved());
					}
				}
				else if (this.ingredient.getItem() == Items.redstone && !water)
				{
					for (int i = 0; i < potionTypes.size(); i++)
					{
						newPotionTypes.add(potionTypes.get(i).onExtended());
					}
				}
				else if (this.ingredient.getItem() == Items.fermented_spider_eye && !water)
				{
					for (int i = 0; i < potionTypes.size(); i++)
					{
						newPotionTypes.add(potionTypes.get(i).onInverted());
					}
				}
				else if (this.ingredient.getItem() == Items.gunpowder && !water)
				{
					for (int i = 0; i < potionTypes.size(); i++)
					{
						newPotionTypes.add(potionTypes.get(i).onGunpowderUsed());
					}
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
				{
					potion.getTagCompound().removeTag(PotionType.COMPOUND_NAME);
				}
				
				for (PotionType potionType : newPotionTypes)
				{
					potionType.addPotionTypeToItemStack(potion);
				}
				
				this.brewingItemStacks[potionIndex] = potion;
			}
		}
		
		if (this.ingredient.getItem().hasContainerItem(this.ingredient))
		{
			this.brewingItemStacks[3] = this.ingredient.getItem().getContainerItem(this.brewingItemStacks[3]);
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
	
	public void setBrewTime(int brewTime)
	{
		this.brewTime = brewTime;
	}
	
	@Override
	public int getBrewTime()
	{
		return this.brewTime;
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
	public void updateEntity()
	{
		if (this.brewTime > 0)
		{
			--this.brewTime;
			
			if (this.brewTime == 0)
			{
				this.brewPotions();
				this.spawnXP();
				this.markDirty();
			}
			else if (!this.canBrew())
			{
				this.brewTime = 0;
				this.markDirty();
			}
			else if (this.ingredient != this.brewingItemStacks[3])
			{
				this.brewTime = 0;
				this.markDirty();
			}
		}
		else if (this.canBrew())
		{
			this.brewTime = this.getMaxBrewTime();
			this.ingredient = this.brewingItemStacks[3];
		}
		
		int filledSlots = this.getFilledSlots();
		
		if (filledSlots != this.filledSlots)
		{
			this.filledSlots = filledSlots;
			this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, filledSlots, 2);
		}
		
		super.updateEntity();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		this.brewingItemStacks = new ItemStack[this.getSizeInventory()];
		
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte slotID = tag.getByte("Slot");
			
			if (slotID >= 0 && slotID < this.brewingItemStacks.length)
			{
				this.brewingItemStacks[slotID] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		
		this.brewTime = nbt.getShort("BrewTime");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setShort("BrewTime", (short) this.brewTime);
		NBTTagList list = new NBTTagList();
		
		for (int i = 0; i < this.brewingItemStacks.length; ++i)
		{
			if (this.brewingItemStacks[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				this.brewingItemStacks[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		
		nbt.setTag("Items", list);
	}
	
	@Override
	public int getSizeInventory()
	{
		return this.brewingItemStacks.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int slotID)
	{
		return slotID >= 0 && slotID < this.brewingItemStacks.length ? this.brewingItemStacks[slotID] : null;
	}
	
	@Override
	public ItemStack decrStackSize(int slotID, int amount)
	{
		if (slotID >= 0 && slotID < this.brewingItemStacks.length)
		{
			ItemStack stack = this.brewingItemStacks[slotID];
			this.brewingItemStacks[slotID] = null;
			return stack;
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slotID)
	{
		if (slotID >= 0 && slotID < this.brewingItemStacks.length)
		{
			ItemStack stack = this.brewingItemStacks[slotID];
			this.brewingItemStacks[slotID] = null;
			return stack;
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int slotID, ItemStack stack)
	{
		if (slotID >= 0 && slotID < this.brewingItemStacks.length)
		{
			this.brewingItemStacks[slotID] = stack;
		}
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		this.thePlayer = player;
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}
	
	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into
	 * the given slot.
	 */
	public boolean isStackValidForSlot(int slotID, ItemStack stack)
	{
		return slotID == 3 && PotionType.isPotionIngredient(stack);
	}
}
