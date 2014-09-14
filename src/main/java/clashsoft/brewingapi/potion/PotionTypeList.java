package clashsoft.brewingapi.potion;

import java.util.ArrayList;
import java.util.List;

import clashsoft.brewingapi.item.ItemPotion2;
import clashsoft.brewingapi.potion.type.IPotionType;
import clashsoft.brewingapi.potion.type.PotionType;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;

/**
 * A {@link ArrayList} of {@link IPotionType} that automatically syncs changes
 * made to it with the corresponding {@link NBTTagList} containing the potion
 * types of an {@link ItemStack} stack compound.
 * 
 * @author Clashsoft
 */
public class PotionTypeList extends ArrayList<IPotionType>
{
	protected ItemStack	stack;
	
	public PotionTypeList()
	{
		super();
	}
	
	public PotionTypeList(ItemStack stack)
	{
		this(stack, true);
	}
	
	public PotionTypeList(ItemStack stack, boolean oldEffects)
	{
		super();
		this.stack = stack;
		this.load(stack, oldEffects);
	}
	
	public ItemStack getPotion()
	{
		return this.stack;
	}
	
	public boolean isWater()
	{
		return ((ItemPotion2) this.stack.getItem()).isWater(this.stack);
	}
	
	public boolean isSplash()
	{
		return ((ItemPotion2) this.stack.getItem()).isSplash(this.stack);
	}
	
	public void setSplash(boolean flag)
	{
		((ItemPotion2) this.stack.getItem()).setSplash(this.stack, flag);
	}
	
	public void setStack(ItemStack stack)
	{
		this.load(stack, true);
	}
	
	/**
	 * Loads this {@link PotionTypeList} from the given {@link ItemStack}
	 * {@code stack} the syncs the list with it. If {@code oldEffects} is true
	 * and the stack NBT was {@code null}, this method searches for effects that
	 * were stored in the stack's damage value.
	 * 
	 * @param stack
	 *            the stack
	 * @param oldEffects
	 */
	public void load(ItemStack stack, boolean oldEffects)
	{
		this.stack = stack;
		
		boolean flag1 = oldEffects;
		if (stack.stackTagCompound != null)
		{
			flag1 = false;
			
			NBTTagList tagList = (NBTTagList) stack.stackTagCompound.getTag(IPotionType.COMPOUND_NAME);
			if (tagList != null)
			{
				for (int i = 0; i < tagList.tagCount(); i++)
				{
					NBTTagCompound compound = tagList.getCompoundTagAt(i);
					IPotionType type = PotionType.getFromNBT(compound);
					if (type != null)
					{
						this.add(type);
					}
				}
			}
		}
		
		if (flag1)
		{
			List<PotionEffect> effects = ((ItemPotion2) stack.getItem()).getSuperEffects(stack);
			
			if (effects != null && !effects.isEmpty())
			{
				for (PotionEffect effect : effects)
				{
					IPotionType potionType = PotionType.getFromEffect(effect);
					if (potionType != null)
					{
						this.add(potionType);
					}
				}
			}
		}
	}
	
	public void save()
	{
		ItemStack stack = this.stack;
		NBTTagList tagList = new NBTTagList();
		for (IPotionType type : this)
		{
			NBTBase nbt1 = toNBT(type);
			tagList.appendTag(nbt1);
		}
		
		if (stack.stackTagCompound == null)
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
		stack.stackTagCompound.setTag(IPotionType.COMPOUND_NAME, tagList);
	}
	
	private static NBTBase toNBT(IPotionType e)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		e.writeToNBT(nbt);
		return nbt;
	}
}
