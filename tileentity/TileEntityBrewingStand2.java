package clashsoft.brewingapi.tileentity;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

import clashsoft.brewingapi.BrewingLoader;
import clashsoft.brewingapi.brewing.Brewing;
import clashsoft.brewingapi.brewing.BrewingBase;
import clashsoft.brewingapi.item.ItemPotion2;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityBrewingStand2 extends TileEntityBrewingStand implements IInventory
{
	public EntityPlayer thePlayer = null;

	private static final int[] field_102017_a = new int[] {3};
	private static final int[] field_102016_b = new int[] {0, 1, 2};

	/** The itemstacks currently placed in the slots of the brewing stand */
	private ItemStack[] brewingItemStacks = new ItemStack[4];
	private int brewTime;

	/**
	 * an integer with each bit specifying whether that slot of the stand contains a potion
	 */
	private int filledSlots;
	private ItemStack ingredient;

	public static int maxBrewTime = 400;

	public TileEntityBrewingStand2()
	{

	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory()
	{
		return this.brewingItemStacks.length;
	}

	/**
	 * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
	 * ticks and creates a new spawn inside its implementation.
	 */
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
					f += Brewing.getExperience(is);
				}
			}

			if (f == 0.0F)
			{
				i = 0;
			}
			else if (f < 1.0F)
			{
				j = MathHelper.floor_float((float)i * f);

				if (j < MathHelper.ceiling_float_int((float)i * f) && (float)Math.random() < (float)i * f - (float)j)
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
			return 40;
		}
		return 400;
	}

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

			if (!Item.itemsList[var1.itemID].isPotionIngredient() && !Brewing.isPotionIngredient(var1))
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
							if (brewingItemStacks[var3].getItem() instanceof ItemPotion2 && ((ItemPotion2)brewingItemStacks[var3].getItem()).isSplash(brewingItemStacks[var3].getItemDamage()) || brewingItemStacks[var3].getItemDamage() == 0)
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
						if (brewingItemStacks[var3] != null)
						{
							if (brewingItemStacks[var3].getItemDamage() == 0)
							{
								var2 = true;
							}
							for (Object o : ItemPotion2.getEffects(brewingItemStacks[var3]))
							{
								if (((Brewing)o).isImprovable())
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
						if (brewingItemStacks[var3] != null)
						{
							if (brewingItemStacks[var3].getItemDamage() == 0)
							{
								var2 = true;
							}
							for (Object o : ItemPotion2.getEffects(brewingItemStacks[var3]))
							{
								if (((Brewing)o).isExtendable())
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
						if (brewingItemStacks[var3] != null)
						{
							if (brewingItemStacks[var3].getItemDamage() == 0)
							{
								var2 = true;
							}
							for (Object o : ItemPotion2.getEffects(brewingItemStacks[var3]))
							{
								Brewing b = (Brewing)o;

								if (b.getOpposite() != null)
								{
									var2 = true;
									break;
								}
							}
						}
					}
				}
				else if (!Brewing.isSpecialIngredient(ingredient))
				{
					for (int var3 = 0; var3 < 3; var3++)
					{
						if (brewingItemStacks[var3] != null)
						{
							if (brewingItemStacks[var3].getItemDamage() == 0)
							{
								if (BrewingBase.getBrewingBaseFromIngredient(var1) != null)
								{
									var2 = true;
									break;
								}
							}
							else if (Brewing.getBrewingFromItemStack(var1) != null)
							{
								Brewing stackBase = Brewing.getBrewingFromItemStack(brewingItemStacks[var3]);
								Brewing requiredBase = Brewing.getBrewingFromIngredient(var1).getBase();
								if (stackBase instanceof BrewingBase && requiredBase instanceof BrewingBase && ((BrewingBase)stackBase).basename == ((BrewingBase)requiredBase).basename)
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
							if (Brewing.getHandlerForIngredient(var1) != null && !Brewing.getHandlerForIngredient(var1).canApplyIngredient(var1, brewingItemStacks[var3]))
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
			ItemStack var1 = this.brewingItemStacks[3];
			ingredient = var1;

			for (int var2 = 0; var2 < 3; ++var2)
			{	
				if (this.brewingItemStacks[var2] != null)
				{
					NBTTagCompound compound = this.brewingItemStacks[var2].stackTagCompound;
					NBTTagList list = (compound != null) ? compound.getTagList("Brewing") : (NBTTagList)null;
					Brewing[] brewings = new Brewing[list != null ? list.tagCount() : 1];
					if (ingredient.getItem() == Item.glowstone)
					{
						if (brewingItemStacks[var2].getItemDamage() == 0)
						{
							brewings[0] = BrewingLoader.thick;
						}
						else
						{
							for (int var3 = 0; var3 < list.tagCount(); var3++)
							{
								Brewing brewing = (list != null) ? Brewing.readFromNBT((NBTTagCompound)list.tagAt(var3)) : BrewingLoader.awkward;
								if (brewing != BrewingLoader.awkward)
								{
									brewings[var3] = brewing.onImproved();
								}
							}
						}
					}
					else if (ingredient.getItem() == Item.redstone)
					{
						if (brewingItemStacks[var2].getItemDamage() == 0)
						{
							brewings[0] = BrewingLoader.thin;
						}
						else
						{
							for (int var3 = 0; var3 < list.tagCount(); var3++)
							{
								Brewing brewing = (list != null) ? Brewing.readFromNBT((NBTTagCompound)list.tagAt(var3)) : BrewingLoader.awkward;
								if (brewing != BrewingLoader.awkward)
								{
									brewings[var3] = brewing.onExtended();
								}
							}
						}
					}
					else if (ingredient.getItem() == Item.fermentedSpiderEye)
					{
						if (brewingItemStacks[var2].getItemDamage() == 0)
						{
							brewings[0] = Brewing.getBrewingFromIngredient(ingredient);
						}
						else
						{
							for (int var3 = 0; var3 < list.tagCount(); var3++)
							{
								Brewing brewing = (list != null) ? Brewing.readFromNBT((NBTTagCompound)list.tagAt(var3)) : BrewingLoader.awkward;
								brewing = brewing.getOpposite() != null ? brewing.getOpposite() : brewing;
								brewing.setOpposite(null);
								brewings[var3] = brewing;
							}
						}
					}
					else if (ingredient.getItem() == Item.netherStalkSeeds)
					{
						brewings[0] = BrewingLoader.awkward;
					}
					else if (ingredient.getItem() == Item.gunpowder)
					{
						if (list != null)
						{
							for (int i = 0; i < list.tagCount(); i++)
							{
								brewings[i] = Brewing.readFromNBT((NBTTagCompound)list.tagAt(i));
								if (brewings[i] != null && brewings[i].getEffect() != null && brewings[i].getEffect().getPotionID() > 0)
								{
									brewings[i].setEffect(new PotionEffect(brewings[i].getEffect().getPotionID(), MathHelper.ceiling_double_int(brewings[i].getEffect().getDuration() * 0.75D), brewings[i].getEffect().getAmplifier()));
								}
							}
						}
						else
						{
							brewings[0] = BrewingLoader.awkward;
						}
					}
					else if (brewingItemStacks[var2].getItemDamage() == 0)
					{
						if (BrewingBase.getBrewingBaseFromIngredient(ingredient) != null)
						{
							brewings[0] = BrewingBase.getBrewingBaseFromIngredient(ingredient);
						}
					}
					else if (!Brewing.isSpecialIngredient(ingredient))
					{
						Brewing stackBase = Brewing.getBrewingFromItemStack(brewingItemStacks[var2]);
						Brewing requiredBase = Brewing.getBrewingFromIngredient(ingredient).getBase();
						if (((BrewingBase)stackBase).basename == ((BrewingBase)requiredBase).basename)
						{
							brewings[0] = Brewing.getBrewingFromIngredient(ingredient);
						}
					}
					else
					{
						brewingItemStacks[var2] = Brewing.getHandlerForIngredient(ingredient).applyIngredient(var1, brewingItemStacks[var2]);
						return;
					}

					if (compound != null)
					{
						compound.removeTag("Brewing");
					}
					if (ingredient.getItem() == Item.fermentedSpiderEye)
					{
						for (Brewing brewing : Brewing.removeDuplicates(brewings))
						{
							if (brewing != null)
							{
								int damage = (ingredient.getItem() == Item.gunpowder || this.brewingItemStacks[var2].getItemDamage() == 2) ? 2 : 1;
								this.brewingItemStacks[var2].setItemDamage(damage);
								brewing.addBrewingToItemStack(this.brewingItemStacks[var2]);
							}
						}	
					}
					else
					{
						for (Brewing brewing : brewings)
						{
							if (brewing != null)
							{
								int damage = (ingredient.getItem() == Item.gunpowder || this.brewingItemStacks[var2].getItemDamage() == 2) ? 2 : 1;
								this.brewingItemStacks[var2].setItemDamage(damage);
								brewing.addBrewingToItemStack(this.brewingItemStacks[var2]);
							}
						}
					}
				}
			}

			if (Item.itemsList[var1.itemID].hasContainerItem())
			{
				this.brewingItemStacks[3] = Item.itemsList[var1.itemID].getContainerItemStack(brewingItemStacks[3]);
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
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
		this.brewingItemStacks = new ItemStack[this.getSizeInventory()];

		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
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
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setShort("BrewTime", (short)this.brewTime);
		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < this.brewingItemStacks.length; ++var3)
		{
			if (this.brewingItemStacks[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte)var3);
				this.brewingItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		par1NBTTagCompound.setTag("Items", var2);
	}

	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int par1)
	{
		return par1 >= 0 && par1 < this.brewingItemStacks.length ? this.brewingItemStacks[par1] : null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
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
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
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
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		if (par1 >= 0 && par1 < this.brewingItemStacks.length)
		{
			this.brewingItemStacks[par1] = par2ItemStack;
		}
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	 * this more of a set than a get?*
	 */
	public int getInventoryStackLimit()
	{
		return 1;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		thePlayer = par1EntityPlayer;
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	public void openChest() {}

	public void closeChest() {}

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

	@SideOnly(Side.CLIENT)
	public void setBrewTime(int par1)
	{
		this.brewTime = par1;
	}

	/**
	 * returns an integer with each bit specifying wether that slot of the stand contains a potion
	 */
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
