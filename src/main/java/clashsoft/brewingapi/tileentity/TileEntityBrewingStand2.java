package clashsoft.brewingapi.tileentity;

import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.potion.PotionTypeList;
import clashsoft.brewingapi.potion.recipe.IPotionRecipe;
import clashsoft.brewingapi.potion.recipe.PotionRecipes;
import clashsoft.brewingapi.potion.type.PotionType;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraftforge.common.util.Constants;

public class TileEntityBrewingStand2 extends TileEntityBrewingStand
{
	public EntityPlayer			thePlayer			= null;
	
	private ItemStack[]			brewingItemStacks	= new ItemStack[4];
	private int					brewTime;
	
	private PotionTypeList[]	potionTypes = new PotionTypeList[3];
	
	private int					filledSlots;
	
	public TileEntityBrewingStand2()
	{
		
	}
	
	public void spawnXP()
	{
		if (!this.getWorldObj().isRemote)
		{
			float f = 0F;
			for (int i = 0; i < 3; i++)
			{
				ItemStack stack = this.brewingItemStacks[i];
				if (stack != null)
				{
					f += PotionType.getExperience(stack);
				}
			}
			
			int i = Math.round(f);
			int j;
			while (i > 0)
			{
				j = EntityXPOrb.getXPSplit(i);
				i -= j;
				this.getWorldObj().spawnEntityInWorld(new EntityXPOrb(this.getWorldObj(), this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, j));
			}
		}
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
		ItemStack ingredient = this.brewingItemStacks[3];
		int count = 0;
		label1: for (int i = 0; i < 3; i++)
		{
			PotionTypeList potionTypes = this.potionTypes[i];
			if (potionTypes != null)
			{
				for (IPotionRecipe recipe : PotionRecipes.getRecipes())
				{
					if (recipe.canApply(ingredient, potionTypes))
					{
						count++;
						continue label1;
					}
				}
				return false;
			}
		}
		return count > 0;
	}
	
	private void brewPotions()
	{
		ItemStack ingredient = this.brewingItemStacks[3];
		
		for (int i = 0; i < 3; i++)
		{
			PotionTypeList potionTypes = this.potionTypes[i];
			if (potionTypes != null)
			{
				for (IPotionRecipe recipe : PotionRecipes.getRecipes())
				{
					if (recipe.canApply(ingredient, potionTypes))
					{
						recipe.apply(potionTypes);
					}
				}
				potionTypes.save();
			}
		}
		
		ItemStack container = ingredient.getItem().getContainerItem(ingredient);
		if (container != null)
		{
			ingredient = container;
		}
		else
		{
			--ingredient.stackSize;
			
			if (ingredient.stackSize <= 0)
			{
				ingredient = null;
			}
		}
		
		this.setInventorySlotContents(3, ingredient);
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
			return 80;
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
		}
		else if (this.canBrew())
		{
			this.brewTime = this.getMaxBrewTime();
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
			this.setInventorySlotContents(slotID, ItemStack.loadItemStackFromNBT(tag));
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
	public ItemStack getStackInSlot(int slot)
	{
		return this.brewingItemStacks[slot];
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack stack = this.brewingItemStacks[slot];
		if (stack != null)
		{
			if (stack.stackSize <= amount)
			{
				this.setInventorySlotContents(slot, null);
				return stack;
			}
			else
			{
				ItemStack stack1 = stack.splitStack(amount);
				if (stack.stackSize <= 0)
				{
					this.setInventorySlotContents(slot, null);
				}
				return stack1;
			}
		}
		return null;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return this.getStackInSlot(slot);
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.brewingItemStacks[slot] = stack;
		
		if (slot < 3)
		{
			if (stack == null)
			{
				this.potionTypes[slot] = null;
			}
			else
			{
				this.potionTypes[slot] = new PotionTypeList(stack);
			}
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
	
	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack stack)
	{
		if (slotID == 3)
		{
			return PotionType.isPotionIngredient(stack);
		}
		else
		{
			return stack.getItem() instanceof ItemPotion2;
		}
	}
}
