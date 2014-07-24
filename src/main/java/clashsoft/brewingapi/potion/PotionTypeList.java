package clashsoft.brewingapi.potion;

import java.util.ArrayList;
import java.util.Collection;
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
	protected ItemStack		stack;
	protected NBTTagList	tagList;
	
	public static PotionTypeList create(ItemStack stack)
	{
		return create(stack, true);
	}
	
	/**
	 * Creates a new {@link PotionTypeList} that is synced with the given
	 * {@link ItemStack} {@code stack}. If {@code oldEffects} is true and the
	 * stack NBT was {@code null}, this method searches for effects that were
	 * stored in the stack's damage value.
	 * 
	 * @param stack
	 *            the stack
	 * @param oldEffects
	 * @return a PotionTypeList instance
	 */
	public static PotionTypeList create(ItemStack stack, boolean oldEffects)
	{
		PotionTypeList list = new PotionTypeList();
		list.load(stack, oldEffects);
		return list;
	}
	
	private PotionTypeList()
	{
		super();
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
		boolean flag1 = false;
		if (stack.stackTagCompound == null)
		{
			flag1 = true;
			stack.stackTagCompound = new NBTTagCompound();
		}
		
		NBTTagList tagList = (NBTTagList) stack.stackTagCompound.getTag(IPotionType.COMPOUND_NAME);
		if (tagList == null)
		{
			tagList = new NBTTagList();
			stack.stackTagCompound.setTag(IPotionType.COMPOUND_NAME, tagList);
		}
		
		this.stack = stack;
		this.tagList = tagList;
		
		if (flag1 && oldEffects)
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
	
	public void addAll(NBTTagList tagList)
	{
		for (int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound compound = tagList.getCompoundTagAt(i);
			IPotionType type = PotionType.getFromNBT(compound);
			if (type != null)
			{
				super.add(type);
			}
		}
	}
	
	private static NBTBase toNBT(IPotionType e)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		e.writeToNBT(nbt);
		return nbt;
	}
	
	@Override
	public boolean add(IPotionType e)
	{
		if (this.tagList != null)
		{
			this.tagList.appendTag(toNBT(e));
		}
		return super.add(e);
	}
	
	@Override
	public IPotionType set(int index, IPotionType e)
	{
		if (this.tagList != null)
		{
			this.tagList.func_150304_a(index, toNBT(e));
		}
		return super.set(index, e);
	}
	
	@Override
	public void clear()
	{
		if (this.tagList != null)
		{
			int tagCount = this.tagList.tagCount();
			while (tagCount > 0)
			{
				this.tagList.removeTag(--tagCount);
			}
		}
		super.clear();
	}
	
	@Override
	public boolean addAll(Collection<? extends IPotionType> c)
	{
		if (this.tagList != null)
		{
			for (IPotionType e : c)
			{
				this.tagList.appendTag(toNBT(e));
			}
		}
		return super.addAll(c);
	}
	
	@Override
	public IPotionType remove(int index)
	{
		if (this.tagList != null)
		{
			this.tagList.removeTag(index);
		}
		return super.remove(index);
	}
	
	@Override
	public boolean remove(Object o)
	{
		if (o instanceof IPotionType)
		{
			int i = this.indexOf((IPotionType) o);
			if (i != -1)
			{
				return this.remove(i) != null;
			}
		}
		return super.remove(o);
	}
}
