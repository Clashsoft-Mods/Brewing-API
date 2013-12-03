package clashsoft.brewingapi.tileentity;

import clashsoft.brewingapi.brewing.PotionType;
import clashsoft.brewingapi.brewing.PotionBase;
import clashsoft.brewingapi.brewing.PotionList;
import clashsoft.brewingapi.item.ItemPotion2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.MathHelper;

public class TileEntityBrewingStand2 extends TileEntityBrewingStand implements IInventory
{
	public EntityPlayer			thePlayer			= null;
	
	private static final int[]	field_102017_a		= new int[] { 3 };
	private static final int[]	field_102016_b		= new int[] { 0, 1, 2 };
	
	/** The itemstacks currently placed in the slots of the brewing stand */
	private ItemStack[]			brewingItemStacks	= new ItemStack[4];
	private int					brewTime;
	
	/**
	 * an integer with each bit specifying whether that slot of the stand
	 * contains a potion
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
	 * Allows the entity to update its state. Overridden in most subclasses,
	 * e.g. the mob spawner uses this to count ticks and creates a new spawn
	 * inside its implementation.
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
			this.brewTime = getMaxBrewTime();
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
			int i = getPotions();
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
		if (thePlayer != null && thePlayer.capabilities.isCreativeMode)
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
		boolean potionSlotsFilled = !(brewingItemStacks[0] == null && brewingItemStacks[1] == null && brewingItemStacks[2] == null);
		if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0 && potionSlotsFilled)
		{
			ItemStack var1 = ingredient = this.brewingItemStacks[3];
			
			if (!Item.itemsList[var1.itemID].isPotionIngredient() && !PotionType.isPotionIngredient(var1))
			{
				return false;
			}
			else
			{
				boolean var2 = false;
				
				if (ingredient.getItem() == Item.gunpowder)
				{
					for (int var3 = 0; var3 < 3; var3++)
					{
						if (brewingItemStacks[var3] != null)
						{
							if (brewingItemStacks[var3].getItem() instanceof ItemPotion2 && ((ItemPotion2) brewingItemStacks[var3].getItem()).isSplash(brewingItemStacks[var3].getItemDamage()) || brewingItemStacks[var3].getItemDamage() == 0)
							{
								var2 = false;
								break;
							}
							else
							{
								var2 = true;
							}
						}
					}
				}
				else if (ingredient.getItem() == Item.glowstone)
				{
					for (int var3 = 0; var3 < 3; var3++)
					{
						if (brewingItemStacks[var3] != null && brewingItemStacks[var3].getItem() instanceof ItemPotion2)
						{
							if (brewingItemStacks[var3].getItemDamage() == 0)
							{
								var2 = true;
							}
							for (Object o : ((ItemPotion2) brewingItemStacks[var3].getItem()).getEffects(brewingItemStacks[var3]))
							{
								if (((PotionType) o).isImprovable())
								{
									var2 = true;
									break;
								}
							}
						}
					}
				}
				else if (ingredient.getItem() == Item.redstone)
				{
					for (int var3 = 0; var3 < 3; var3++)
					{
						if (brewingItemStacks[var3] != null && brewingItemStacks[var3].getItem() instanceof ItemPotion2)
						{
							if (brewingItemStacks[var3].getItemDamage() == 0)
							{
								var2 = true;
							}
							for (Object o : ((ItemPotion2) brewingItemStacks[var3].getItem()).getEffects(brewingItemStacks[var3]))
							{
								if (((PotionType) o).isExtendable())
								{
									var2 = true;
									break;
								}
							}
						}
					}
				}
				else if (ingredient.getItem() == Item.fermentedSpiderEye)
				{
					for (int var3 = 0; var3 < 3; var3++)
					{
						if (brewingItemStacks[var3] != null && brewingItemStacks[var3].getItem() instanceof ItemPotion2)
						{
							if (brewingItemStacks[var3].getItemDamage() == 0)
							{
								var2 = true;
							}
							for (Object o : ((ItemPotion2) brewingItemStacks[var3].getItem()).getEffects(brewingItemStacks[var3]))
							{
								PotionType b = (PotionType) o;
								
								if (b.getInverted() != null)
								{
									var2 = true;
									break;
								}
							}
						}
					}
				}
				else if (!PotionType.hasIngredientHandler(ingredient))
				{
					for (int var3 = 0; var3 < 3; var3++)
					{
						if (brewingItemStacks[var3] != null)
						{
							if (((ItemPotion2) brewingItemStacks[var3].getItem()).isWater(brewingItemStacks[var3].getItemDamage()))
							{
								if (PotionBase.getBrewingBaseFromIngredient(var1) != null)
								{
									var2 = true;
									break;
								}
							}
							else if (PotionType.getFirstBrewing(var1) != null)
							{
								PotionType stackBase = PotionType.getFirstBrewing(brewingItemStacks[var3]);
								PotionType requiredBase = PotionType.getBrewingFromIngredient(var1).getBase();
								if (stackBase instanceof PotionBase && requiredBase instanceof PotionBase && ((PotionBase) stackBase).basename.equals(((PotionBase) requiredBase).basename))
								{
									var2 = true;
								}
								else
								{
									var2 = false;
									break;
								}
							}
						}
					}
				}
				else
				{
					for (int var3 = 0; var3 < 3; var3++)
					{
						if (brewingItemStacks[var3] != null)
						{
							if (PotionType.getHandlerForIngredient(var1) != null && !PotionType.getHandlerForIngredient(var1).canApplyIngredient(var1, brewingItemStacks[var3]))
							{
								var2 = false;
								break;
							}
							else
							{
								var2 = true;
							}
						}
					}
				}
				
				return var2;
			}
		}
		else
		{
			return false;
		}
	}
	
	private void brewPotions()
	{
		if (this.canBrew())
		{
			ItemStack ingredient = this.brewingItemStacks[3];
			this.ingredient = ingredient;
			
			for (int potionIndex = 0; potionIndex < 3; ++potionIndex)
			{
				if (this.brewingItemStacks[potionIndex] != null)
				{
					if (this.brewingItemStacks[potionIndex].getTagCompound() == null)
						this.brewingItemStacks[potionIndex].setTagCompound(new NBTTagCompound());
					NBTTagCompound compound = this.brewingItemStacks[potionIndex].stackTagCompound;
					
					NBTTagList tagList = compound != null ? compound.getTagList("PotionType") : null;
					PotionType[] brewings = new PotionType[tagList != null && tagList.tagCount() != 0 ? tagList.tagCount() : 1];
					if (tagList != null)
						if (ingredient.getItem() == Item.glowstone)
						{
							if (brewingItemStacks[potionIndex].getItemDamage() == 0)
							{
								brewings[0] = PotionList.thick;
							}
							else
							{
								for (int var3 = 0; var3 < tagList.tagCount(); var3++)
								{
									PotionType potionType = new PotionType();
									potionType.readFromNBT((NBTTagCompound) tagList.tagAt(var3));
									
									if (potionType != PotionList.awkward)
										brewings[var3] = potionType.onImproved();
								}
							}
						}
						else if (ingredient.getItem() == Item.redstone)
						{
							if (brewingItemStacks[potionIndex].getItemDamage() == 0)
							{
								brewings[0] = PotionList.thin;
							}
							else
							{
								for (int index = 0; index < tagList.tagCount(); index++)
								{
									
									PotionType potionType = new PotionType();
									potionType.readFromNBT((NBTTagCompound) tagList.tagAt(index));
									
									if (potionType != PotionList.awkward)
										brewings[index] = potionType.onExtended();
								}
							}
						}
						else if (ingredient.getItem() == Item.fermentedSpiderEye)
						{
							if (brewingItemStacks[potionIndex].getItemDamage() == 0)
							{
								brewings[0] = PotionType.getBrewingFromIngredient(ingredient);
							}
							else
							{
								for (int index = 0; index < tagList.tagCount(); index++)
								{
									PotionType potionType = new PotionType();
									potionType.readFromNBT((NBTTagCompound) tagList.tagAt(index));
									
									brewings[index] = potionType.getInverted();
								}
							}
						}
						else if (ingredient.getItem() == Item.netherStalkSeeds)
						{
							brewings[0] = PotionList.awkward;
						}
						else if (ingredient.getItem() == Item.gunpowder)
						{
							for (int index = 0; index < tagList.tagCount(); index++)
							{
								PotionType potionType = new PotionType();
								potionType.readFromNBT((NBTTagCompound) tagList.tagAt(index));
								
								if (brewings[index] != null && brewings[index].getEffect() != null && brewings[index].getEffect().getPotionID() > 0)
								{
									brewings[index].setEffect(new PotionEffect(brewings[index].getEffect().getPotionID(), MathHelper.ceiling_double_int(brewings[index].getEffect().getDuration() * 0.75D), brewings[index].getEffect().getAmplifier()));
								}
							}
						}
						else if (((ItemPotion2) brewingItemStacks[potionIndex].getItem()).isWater(brewingItemStacks[potionIndex].getItemDamage()))
						{
							if (PotionBase.getBrewingBaseFromIngredient(ingredient) != null)
							{
								brewings[0] = PotionBase.getBrewingBaseFromIngredient(ingredient);
							}
						}
						else if (!PotionType.hasIngredientHandler(ingredient))
						{
							PotionType stackBase = PotionType.getFirstBrewing(brewingItemStacks[potionIndex]);
							PotionType requiredBase = PotionType.getBrewingFromIngredient(ingredient).getBase();
							if (((PotionBase) stackBase).basename.equals(((PotionBase) requiredBase).basename))
							{
								brewings[0] = PotionType.getBrewingFromIngredient(ingredient);
							}
						}
						else
						{
							brewingItemStacks[potionIndex] = PotionType.getHandlerForIngredient(ingredient).applyIngredient(ingredient, brewingItemStacks[potionIndex]);
							return;
						}
					
					if (compound != null)
					{
						compound.removeTag("PotionType");
					}
					if (ingredient.getItem() == Item.fermentedSpiderEye)
					{
						for (PotionType potionType : PotionType.removeDuplicates(brewings))
						{
							if (potionType != null)
							{
								int damage = (ingredient.getItem() == Item.gunpowder || this.brewingItemStacks[potionIndex].getItemDamage() == 2) ? 2 : 1;
								this.brewingItemStacks[potionIndex].setItemDamage(damage);
								potionType.addBrewingToItemStack(this.brewingItemStacks[potionIndex]);
							}
						}
					}
					else
					{
						for (PotionType potionType : brewings)
						{
							if (potionType != null)
							{
								int damage = (ingredient.getItem() == Item.gunpowder || this.brewingItemStacks[potionIndex].getItemDamage() == 2) ? 2 : 1;
								this.brewingItemStacks[potionIndex].setItemDamage(damage);
								potionType.addBrewingToItemStack(this.brewingItemStacks[potionIndex]);
							}
						}
					}
				}
			}
			
			if (Item.itemsList[ingredient.itemID].hasContainerItem())
			{
				this.brewingItemStacks[3] = Item.itemsList[ingredient.itemID].getContainerItemStack(brewingItemStacks[3]);
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
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
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
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
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
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
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
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended. *Isn't this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}
	
	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		thePlayer = par1EntityPlayer;
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
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
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
	 * returns an integer with each bit specifying wether that slot of the stand
	 * contains a potion
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
	
	/**
	 * Get the size of the side inventory.
	 */
	public int[] getSizeInventorySide(int par1)
	{
		return par1 == 1 ? field_102017_a : field_102016_b;
	}
}
